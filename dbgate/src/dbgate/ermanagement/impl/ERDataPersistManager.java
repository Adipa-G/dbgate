package dbgate.ermanagement.impl;

import dbgate.DBClassStatus;
import dbgate.DBColumnType;
import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.context.EntityFieldValue;
import dbgate.ermanagement.context.IEntityContext;
import dbgate.ermanagement.context.IEntityFieldValueList;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.context.impl.EntityRelationFieldValueList;
import dbgate.ermanagement.context.impl.EntityTypeFieldValueList;
import dbgate.ermanagement.exceptions.*;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.impl.utils.ERSessionUtils;
import dbgate.ermanagement.impl.utils.MiscUtils;
import dbgate.ermanagement.impl.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Apr 3, 2011
 * Time: 3:26:54 PM
 */
public class ERDataPersistManager extends ERDataCommonManager
{
    public ERDataPersistManager(IDBLayer dbLayer, IERLayerConfig config)
    {
        super(dbLayer,config);
    }

    public void save(ServerDBClass entity, Connection con ) throws PersistException
    {
        try
        {
            ERSessionUtils.initSession(entity);
            ERDataManagerUtils.registerTypes(entity);
            trackAndCommitChanges(entity, con);

            Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(entity.getClass(),new Class[]{ServerDBClass.class});
            ERDataManagerUtils.reverse(typeList); //use reverse order not to break the super to sub constraints
            for (Class type : typeList)
            {
                saveForType(entity,type, con);
            }
            entity.setStatus(DBClassStatus.UNMODIFIED);
            ERSessionUtils.destroySession(entity);
        }
        catch (Exception e)
        {
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE,e.getMessage(),e);
            throw new PersistException(e.getMessage(),e);
        }
    }

    private void trackAndCommitChanges(ServerDBClass serverDBClass, Connection con)
            throws SequenceGeneratorInitializationException, InvocationTargetException
            , NoSuchMethodException, FieldCacheMissException, IllegalAccessException
            , IntegrityConstraintViolationException, NoFieldsFoundException, SQLException
            , TableCacheMissException, QueryBuildingException, NoMatchingColumnFoundException
            , RetrievalException, InstantiationException
    {
        IEntityContext entityContext = serverDBClass.getContext();
        Collection<ITypeFieldValueList> originalChildren;

        if (entityContext != null)
        {
            if (config.isAutoTrackChanges())
            {
                if (checkForModification(serverDBClass,con,entityContext))
                {
                    MiscUtils.modify(serverDBClass);
                }
            }
            originalChildren = entityContext.getChangeTracker().getChildEntityKeys();
        }
        else
        {
            originalChildren = getChildEntityValueListIncludingDeletedStatusItems(serverDBClass);
        }

        Collection<ITypeFieldValueList> currentChildren = getChildEntityValueListExcludingDeletedStatusItems(serverDBClass);
        validateForChildDeletion(serverDBClass,currentChildren);
        Collection<ITypeFieldValueList> deletedChildren = ERDataManagerUtils.findDeletedChildren(originalChildren,currentChildren);
        deleteOrphanChildren(con,deletedChildren);
    }

    private void saveForType(ServerDBClass entity,Class type, Connection con) throws TableCacheMissException
            , PersistException, FieldCacheMissException, InvocationTargetException, NoSuchMethodException
            , IllegalAccessException, SQLException, QueryBuildingException, NoMatchingColumnFoundException
            , IncorrectStatusException, SequenceGeneratorInitializationException, NoFieldsFoundException
            , DataUpdatedFromAnotherSourceException
    {
        String tableName = CacheManager.tableCache.getTableName(type);
        if (tableName == null)
        {
            return;
        }

        Collection<IDBRelation> dbRelations = CacheManager.fieldCache.getRelations(type);
        for (IDBRelation relation : dbRelations)
        {
            if (relation.isReverseRelationship())
            {
                continue;
            }
            Collection<ServerDBClass> childObjects = ERDataManagerUtils.getRelationEntities(entity, relation);
            if (childObjects != null)
            {
                if (relation.isNonIdentifyingRelation())
                {
                    for (DBRelationColumnMapping mapping : relation.getTableColumnMappings())
                    {
                        setParentRelationFieldsForNonIdentifyingRelations(entity,childObjects,mapping);
                    }
                }
            }
        }

        ITypeFieldValueList fieldValues = ERDataManagerUtils.extractTypeFieldValues(entity,type);
        if (entity.getStatus() == DBClassStatus.UNMODIFIED)
        {
            //do nothing
        }
        else if (entity.getStatus() == DBClassStatus.NEW)
        {
            insert(entity,fieldValues,type,con);
        }
        else if (entity.getStatus() == DBClassStatus.MODIFIED)
        {
            if (config.isCheckVersion())
            {
                if (!versionValidated(entity,type,con))
                {
                     throw new DataUpdatedFromAnotherSourceException(String.format("The type %s updated from another transaction",type));
                }
                ERDataManagerUtils.incrementVersion(fieldValues);
                setValues(entity,fieldValues);
            }
            update(fieldValues,type,con);
        }
        else if (entity.getStatus() == DBClassStatus.DELETED)
        {
            delete(fieldValues,type,con);
        }
        else
        {
            String message = String.format("In-corret status for class %s",type.getCanonicalName());
            throw new IncorrectStatusException(message);
        }

        fieldValues = ERDataManagerUtils.extractTypeFieldValues(entity,type);
        IEntityContext entityContext = entity.getContext();
        if (entityContext != null)
        {
            entityContext.getChangeTracker().getFields().clear();
            entityContext.getChangeTracker().getFields().addAll(fieldValues.getFieldValues());
        }

        ERSessionUtils.addToSession(entity,ERDataManagerUtils.extractEntityKeyValues(entity));


        for (IDBRelation relation : dbRelations)
        {
            if (relation.isReverseRelationship()
                    || relation.isNonIdentifyingRelation())
            {
                continue;
            }
            Collection<ServerDBClass> childObjects = ERDataManagerUtils.getRelationEntities(entity, relation);
            if (childObjects != null)
            {

                setRelationObjectKeyValues(fieldValues,type,childObjects,relation);
                for (ServerDBClass fieldObject : childObjects)
                {
                    IEntityFieldValueList childEntityKeyList = ERDataManagerUtils.extractEntityKeyValues(fieldObject);
                    if (ERSessionUtils.existsInSession(entity,childEntityKeyList))
                    {
                        continue;
                    }
                    ERSessionUtils.transferSession(entity,fieldObject);
                    if (fieldObject.getStatus() != DBClassStatus.DELETED) //deleted items are already deleted
                    {
                        fieldObject.persist(con);
                        ERSessionUtils.addToSession(entity,childEntityKeyList);
                    }
                }
            }
        }
    }

    private void insert(ServerDBClass entity,ITypeFieldValueList valueTypeList,Class type,Connection con) throws TableCacheMissException
            , QueryBuildingException, FieldCacheMissException, SQLException, NoSuchMethodException
            , InvocationTargetException, IllegalAccessException
    {
        StringBuilder logSb = new StringBuilder();
        String query = CacheManager.queryCache.getInsertQuery(type);
        PreparedStatement ps = con.prepareStatement(query);

        boolean showQuery = config.isShowQueries();
        if (showQuery)
        {
            logSb.append(query);
        }
        int i = 0;
        for (EntityFieldValue fieldValue : valueTypeList.getFieldValues())
        {
            IDBColumn dbColumn = fieldValue.getDbColumn();
            Object columnValue = null;

            if (dbColumn.isReadFromSequence()
                    && dbColumn.getSequenceGenerator() != null)
            {
                columnValue = dbColumn.getSequenceGenerator().getNextSequenceValue(con);

                Method keySetter = CacheManager.methodCache.getSetter(entity,dbColumn);
                keySetter.invoke(entity,columnValue);
            }
            else
            {
                columnValue = fieldValue.getValue();
            }
            if (showQuery)
            {
                logSb.append(" ,").append(dbColumn.getColumnName()).append("=").append(columnValue);
            }
            dbLayer.getDataManipulate().setToPreparedStatement(ps,columnValue,i+1,dbColumn);
            i++;
        }
        if (showQuery)
        {
            Logger.getLogger(config.getLoggerName()).info(logSb.toString());
        }
        ps.execute();
        DBMgmtUtility.close(ps);
    }

    private void update(ITypeFieldValueList valueTypeList,Class type,Connection con) throws TableCacheMissException
            , QueryBuildingException, FieldCacheMissException, SQLException
    {
        StringBuilder logSb = new StringBuilder();
        String query = CacheManager.queryCache.getUpdateQuery(type);

        ArrayList<EntityFieldValue> keys = new ArrayList<EntityFieldValue>();
        ArrayList<EntityFieldValue> values = new ArrayList<EntityFieldValue>();
        for (EntityFieldValue fieldValue : valueTypeList.getFieldValues())
        {
            if (fieldValue.getDbColumn().isKey())
            {
                keys.add(fieldValue);
            }
            else
            {
                values.add(fieldValue);
            }
        }

        boolean showQuery = config.isShowQueries();
        if (showQuery)
        {
            logSb.append(query);
        }

        PreparedStatement ps = con.prepareStatement(query);
        int count = 0;
        for (EntityFieldValue fieldValue : values)
        {
            if (showQuery)
            {
                logSb.append(" ,").append(fieldValue.getDbColumn().getColumnName()).append("=").append(fieldValue.getValue());
            }
            dbLayer.getDataManipulate().setToPreparedStatement(ps, fieldValue.getValue(), ++count, fieldValue.getDbColumn());
        }

        for (EntityFieldValue fieldValue : keys)
        {
            if (showQuery)
            {
                logSb.append(" ,").append(fieldValue.getDbColumn().getColumnName()).append("=").append(fieldValue.getValue());
            }
            dbLayer.getDataManipulate().setToPreparedStatement(ps, fieldValue.getValue(), ++count, fieldValue.getDbColumn());
        }
        if (showQuery)
        {
            Logger.getLogger(config.getLoggerName()).info(logSb.toString());
        }
        ps.execute();
        DBMgmtUtility.close(ps);
    }

    private void delete(ITypeFieldValueList valueTypeList,Class type,Connection con) throws TableCacheMissException
            , QueryBuildingException, FieldCacheMissException, SQLException
    {
        StringBuilder logSb = new StringBuilder();
        String query = CacheManager.queryCache.getDeleteQuery(type);
        ArrayList<EntityFieldValue> keys = new ArrayList<EntityFieldValue>();

        for (EntityFieldValue fieldValue : valueTypeList.getFieldValues())
        {
            if (fieldValue.getDbColumn().isKey())
            {
                keys.add(fieldValue);
            }
        }

        boolean showQuery = config.isShowQueries();
        if (showQuery)
        {
            logSb.append(query);
        }
        PreparedStatement ps = con.prepareStatement(query);
        for (int i = 0; i < keys.size(); i++)
        {
            EntityFieldValue fieldValue = keys.get(i);

            if (showQuery)
            {
                logSb.append(" ,").append(fieldValue.getDbColumn().getColumnName()).append("=").append(fieldValue.getValue());
            }
            dbLayer.getDataManipulate().setToPreparedStatement(ps,fieldValue.getValue(),i+1,fieldValue.getDbColumn());
        }
        if (showQuery)
        {
            Logger.getLogger(config.getLoggerName()).info(logSb.toString());
        }
        ps.execute();
        DBMgmtUtility.close(ps);
    }

    private void setRelationObjectKeyValues(ITypeFieldValueList valueTypeList,Class type,Collection<ServerDBClass> childObjects
            ,IDBRelation relation) throws FieldCacheMissException, InvocationTargetException, NoMatchingColumnFoundException
            , NoSuchMethodException, IllegalAccessException, NoFieldsFoundException, SequenceGeneratorInitializationException
    {
        Collection<IDBColumn> columns = CacheManager.fieldCache.getColumns(type);
        for (DBRelationColumnMapping mapping : relation.getTableColumnMappings())
        {
            IDBColumn matchColumn = ERDataManagerUtils.findColumnByAttribute(columns,mapping.getFromField());
            EntityFieldValue fieldValue = valueTypeList.getFieldValue(matchColumn.getAttributeName());

            if (fieldValue != null)
            {
                setChildPrimaryKeys(fieldValue,childObjects,mapping);
            }
            else
            {
                String message = String.format("The column %s does not have a matching column in the object %s",matchColumn.getColumnName(), valueTypeList.getType().getName());
                throw new NoMatchingColumnFoundException(message);
            }
        }
    }

    private void setChildPrimaryKeys(EntityFieldValue parentFieldValue,Collection<ServerDBClass> childObjects
            ,DBRelationColumnMapping mapping) throws FieldCacheMissException, NoSuchMethodException, InvocationTargetException
            , IllegalAccessException, NoMatchingColumnFoundException, SequenceGeneratorInitializationException
            , NoFieldsFoundException
    {
        ServerRODBClass firstObject = null;
        if (childObjects.size() > 0)
        {
            firstObject = childObjects.iterator().next();
        }
        if (firstObject == null)
        {
            return;
        }
        ERDataManagerUtils.registerTypes(firstObject);

        boolean foundOnce = false;
        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(firstObject.getClass(),new Class[]{ServerRODBClass.class});
        for (Class type : typeList)
        {
            Collection<IDBColumn> subLevelColumns = CacheManager.fieldCache.getColumns(type);
            IDBColumn subLevelMatchedColumn = ERDataManagerUtils.findColumnByAttribute(subLevelColumns,mapping.getToField());

            if (subLevelMatchedColumn != null)
            {
                foundOnce = true;
                Method setter = CacheManager.methodCache.getSetter(firstObject, subLevelMatchedColumn);
                for (ServerRODBClass dbObject : childObjects)
                {
                    setter.invoke(dbObject, parentFieldValue.getValue());
                }
            }
        }
        if (!foundOnce)
        {
            String message = String.format("The field %s does not have a matching field in the object %s", mapping.getToField(),firstObject.getClass().getName());
            throw new NoMatchingColumnFoundException(message);
        }
    }

    private void setParentRelationFieldsForNonIdentifyingRelations(ServerDBClass parentEntity,Collection<ServerDBClass> childObjects
            ,DBRelationColumnMapping mapping) throws FieldCacheMissException, NoSuchMethodException, InvocationTargetException
            , IllegalAccessException, NoMatchingColumnFoundException, SequenceGeneratorInitializationException
            , NoFieldsFoundException
    {
        ServerRODBClass firstObject = null;
        if (childObjects.size() > 0)
        {
            firstObject = childObjects.iterator().next();
        }
        if (firstObject == null)
        {
            return;
        }
        ERDataManagerUtils.registerTypes(firstObject);

        Class[] parentTypeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(parentEntity.getClass(),new Class[]{ServerRODBClass.class});
        Class[] childTypeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(firstObject.getClass(),new Class[]{ServerRODBClass.class});

        Method setter = null;
        boolean foundOnce = false;
        for (Class type : parentTypeList)
        {
            Collection<IDBColumn> parentColumns = CacheManager.fieldCache.getColumns(type);
            IDBColumn parentMatchedColumn = ERDataManagerUtils.findColumnByAttribute(parentColumns,mapping.getFromField());
            if (parentMatchedColumn != null)
            {
                foundOnce = true;
                setter = CacheManager.methodCache.getSetter(parentEntity, parentMatchedColumn);
            }
        }
        if (!foundOnce)
        {
            String message = String.format("The field %s does not have a matching field in the object %s", mapping.getToField(),firstObject.getClass().getName());
            throw new NoMatchingColumnFoundException(message);
        }

        foundOnce = false;
        for (Class type : childTypeList)
        {
            Collection<IDBColumn> subLevelColumns = CacheManager.fieldCache.getColumns(type);
            IDBColumn childMatchedColumn = ERDataManagerUtils.findColumnByAttribute(subLevelColumns,mapping.getToField());

            if (childMatchedColumn != null)
            {
                foundOnce = true;
                for (ServerRODBClass dbObject : childObjects)
                {
                    ITypeFieldValueList fieldValueList = ERDataManagerUtils.extractTypeFieldValues(dbObject,type);
                    EntityFieldValue childFieldValue = fieldValueList.getFieldValue(childMatchedColumn.getAttributeName());
                    setter.invoke(parentEntity,childFieldValue.getValue());
                }
            }
        }
        if (!foundOnce)
        {
            String message = String.format("The field %s does not have a matching field in the object %s", mapping.getToField(),firstObject.getClass().getName());
            throw new NoMatchingColumnFoundException(message);
        }
    }

    private void validateForChildDeletion(ServerDBClass currentEntity, Collection<ITypeFieldValueList> currentChildren) throws IntegrityConstraintViolationException
    {
        for (ITypeFieldValueList keyValueList : currentChildren)
        {
            EntityRelationFieldValueList entityRelationKeyValueList = (EntityRelationFieldValueList) keyValueList;
            if (entityRelationKeyValueList.getRelation().getDeleteRule() == ReferentialRuleType.RESTRICT
                    && !entityRelationKeyValueList.getRelation().isReverseRelationship()
                    && currentEntity.getStatus() == DBClassStatus.DELETED)
            {
                throw new IntegrityConstraintViolationException(String.format("Cannot delete child object %s as restrict constraint in place",keyValueList.getType().getCanonicalName()));
            }
        }
    }

    private boolean checkForModification(ServerDBClass serverDBClass,Connection con, IEntityContext entityContext)
            throws FieldCacheMissException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
            , SQLException, TableCacheMissException, QueryBuildingException, SequenceGeneratorInitializationException
            , NoFieldsFoundException, NoMatchingColumnFoundException, RetrievalException, InstantiationException
    {
        if (!entityContext.getChangeTracker().isValid())
        {
            FillChangeTrackerValues(serverDBClass, con, entityContext);
        }

        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(serverDBClass.getClass(),new Class[]{ServerDBClass.class});
        for (Class type : typeList)
        {
            Collection<IDBColumn> subLevelColumns = CacheManager.fieldCache.getColumns(type);
            for (IDBColumn subLevelColumn : subLevelColumns)
            {
                if (subLevelColumn.isKey())
                {
                    continue;
                }

                Method getter = CacheManager.methodCache.getGetter(serverDBClass, subLevelColumn.getAttributeName());
                Object value = getter.invoke(serverDBClass);

                EntityFieldValue fieldValue = entityContext.getChangeTracker().getFieldValue(subLevelColumn.getAttributeName());
                boolean isMatch = (fieldValue != null && fieldValue.getValue() == value)
                        || (fieldValue != null && fieldValue.getValue() != null && fieldValue.equals(value));
                if (!isMatch)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void FillChangeTrackerValues(ServerDBClass serverDBClass, Connection con, IEntityContext entityContext)
            throws InvocationTargetException, NoSuchMethodException, FieldCacheMissException
            , IllegalAccessException, SQLException, TableCacheMissException, QueryBuildingException
            , NoFieldsFoundException, SequenceGeneratorInitializationException, NoMatchingColumnFoundException
            , RetrievalException, InstantiationException
    {
        if (serverDBClass.getStatus() == DBClassStatus.NEW
                || serverDBClass.getStatus() == DBClassStatus.DELETED)
        {
            return;
        }

        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(serverDBClass.getClass(), new Class[]{ServerDBClass.class});
        for (Class type : typeList)
        {
            String tableName = CacheManager.tableCache.getTableName(type);
            if (tableName == null)
            {
                continue;
            }

            ITypeFieldValueList values = extractCurrentRowValues(serverDBClass,type,con);
            for (EntityFieldValue fieldValue : values.getFieldValues())
            {
                entityContext.getChangeTracker().getFields().add(fieldValue);
            }

            Collection<IDBRelation> dbRelations = CacheManager.fieldCache.getRelations(type);
            for (IDBRelation relation : dbRelations)
            {
                Collection<ServerRODBClass> children = readRelationChildrenFromDb(serverDBClass,type,con,relation);
                for (ServerRODBClass childEntity : children)
                {
                    ITypeFieldValueList valueTypeList = ERDataManagerUtils.extractRelationKeyValues(childEntity,relation);
                    if (valueTypeList != null)
                    {
                        entityContext.getChangeTracker().getChildEntityKeys().add(valueTypeList);
                    }
                }
            }
        }
    }

    private void deleteOrphanChildren(Connection con,Collection<ITypeFieldValueList> childrenToDelete)
            throws TableCacheMissException, SQLException, QueryBuildingException
            , FieldCacheMissException
    {
        for (ITypeFieldValueList relationKeyValueList : childrenToDelete)
        {
            String table = CacheManager.tableCache.getTableName(relationKeyValueList.getType());
            if (table == null)
            {
                continue;
            }
            if (relationKeyValueList instanceof EntityRelationFieldValueList)
            {
                EntityRelationFieldValueList relationFieldValueList = (EntityRelationFieldValueList)relationKeyValueList;
                if (relationFieldValueList.getRelation().isNonIdentifyingRelation()
                        || relationFieldValueList.getRelation().isReverseRelationship())
                {
                    continue;
                }
            }

            boolean recordExists = false;
            PreparedStatement ps = createRetrievalPreparedStatement(relationKeyValueList,con);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                recordExists = true;
            }
            DBMgmtUtility.close(rs);
            DBMgmtUtility.close(ps);

            if (recordExists)
            {
                delete(relationKeyValueList,relationKeyValueList.getType(),con);
            }
        }
    }

    private boolean versionValidated(ServerRODBClass entity,Class type,Connection con)
            throws InvocationTargetException, NoSuchMethodException, SQLException
            , TableCacheMissException, QueryBuildingException, FieldCacheMissException
            , IllegalAccessException
    {
        Collection<IDBColumn> typeColumns = CacheManager.fieldCache.getColumns(type);
        for (IDBColumn typeColumn : typeColumns)
        {
            if (typeColumn.getColumnType() == DBColumnType.VERSION)
            {
                Object classValue = extractCurrentVersionValue(entity,typeColumn,type,con);
                EntityFieldValue originalFieldValue = entity.getContext().getChangeTracker().getFieldValue(typeColumn.getAttributeName());
                return originalFieldValue != null && classValue == originalFieldValue.getValue()
                        || (originalFieldValue != null && classValue != null && classValue.equals(originalFieldValue.getValue()));
            }
        }

        ITypeFieldValueList fieldValueList = extractCurrentRowValues(entity,type,con);
        if (fieldValueList == null)
        {
            return false;
        }
        for (IDBColumn typeColumn : typeColumns)
        {
            EntityFieldValue classFieldValue = fieldValueList.getFieldValue(typeColumn.getAttributeName());
            EntityFieldValue originalFieldValue = entity.getContext() != null ? entity.getContext().getChangeTracker().getFieldValue(typeColumn.getAttributeName()) : null;
            boolean matches = originalFieldValue != null && classFieldValue != null && classFieldValue.getValue() == originalFieldValue.getValue()
                        || (originalFieldValue != null && classFieldValue != null && classFieldValue.getValue().equals(originalFieldValue.getValue()));
            if (!matches)
            {
                return false;
            }
        }
        return true;
    }

    private Object extractCurrentVersionValue(ServerRODBClass entity,IDBColumn versionColumn,Class type,Connection con)
            throws InvocationTargetException, NoSuchMethodException, FieldCacheMissException
            , IllegalAccessException, SQLException, TableCacheMissException
            , QueryBuildingException
    {
        Object versionValue = null;

        ITypeFieldValueList keyFieldValueList = ERDataManagerUtils.extractTypeKeyValues(entity,type);
        PreparedStatement ps = createRetrievalPreparedStatement(keyFieldValueList,con);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            versionValue = dbLayer.getDataManipulate().readFromResultSet(rs,versionColumn);
        }
        DBMgmtUtility.close(rs);
        DBMgmtUtility.close(ps);

        return versionValue;
    }

    private ITypeFieldValueList extractCurrentRowValues(ServerRODBClass entity,Class type,Connection con)
            throws InvocationTargetException, NoSuchMethodException, FieldCacheMissException
            , IllegalAccessException, SQLException, TableCacheMissException, QueryBuildingException
    {
        ITypeFieldValueList fieldValueList = null;

        ITypeFieldValueList keyFieldValueList = ERDataManagerUtils.extractTypeKeyValues(entity,type);
        PreparedStatement ps = createRetrievalPreparedStatement(keyFieldValueList,con);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            fieldValueList = readValues(type,rs);
        }
        DBMgmtUtility.close(rs);
        DBMgmtUtility.close(ps);

        return fieldValueList;
    }
}
