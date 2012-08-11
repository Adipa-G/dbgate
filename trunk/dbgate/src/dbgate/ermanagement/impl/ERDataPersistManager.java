package dbgate.ermanagement.impl;

import dbgate.*;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.caches.impl.EntityInfo;
import dbgate.ermanagement.context.EntityFieldValue;
import dbgate.ermanagement.context.IEntityContext;
import dbgate.ermanagement.context.IEntityFieldValueList;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.context.impl.EntityRelationFieldValueList;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.common.NoMatchingColumnFoundException;
import dbgate.ermanagement.exceptions.common.StatementExecutionException;
import dbgate.ermanagement.exceptions.common.StatementPreparingException;
import dbgate.ermanagement.exceptions.persist.DataUpdatedFromAnotherSourceException;
import dbgate.ermanagement.exceptions.persist.IncorrectStatusException;
import dbgate.ermanagement.exceptions.persist.IntegrityConstraintViolationException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.impl.utils.ERSessionUtils;
import dbgate.ermanagement.impl.utils.MiscUtils;
import dbgate.ermanagement.impl.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
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
    public ERDataPersistManager(IDBLayer dbLayer, IERLayerStatistics statistics, IERLayerConfig config)
    {
        super(dbLayer, statistics, config);
    }

    public void save(ServerDBClass entity, Connection con) throws PersistException
    {
        try
        {
            ERSessionUtils.initSession(entity);
            trackAndCommitChanges(entity, con);

            Stack<EntityInfo> entityInfoStack = new Stack<>();
            EntityInfo entityInfo = CacheManager.getEntityInfo(entity);
            while (entityInfo != null)
            {
                entityInfoStack.push(entityInfo);
                entityInfo = entityInfo.getSuperEntityInfo();
            }

            while (!entityInfoStack.empty())
            {
                entityInfo = entityInfoStack.pop();
                saveForType(entity, entityInfo.getEntityType(), con);
            }

            entity.setStatus(DBClassStatus.UNMODIFIED);
            ERSessionUtils.destroySession(entity);
        } catch (Exception e)
        {
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE, e.getMessage(), e);
            throw new PersistException(e.getMessage(), e);
        }
    }

    private void trackAndCommitChanges(ServerDBClass serverDBClass, Connection con) throws DbGateException
    {
        IEntityContext entityContext = serverDBClass.getContext();
        Collection<ITypeFieldValueList> originalChildren;

        if (entityContext != null)
        {
            if (config.isAutoTrackChanges())
            {
                if (checkForModification(serverDBClass, con, entityContext))
                {
                    MiscUtils.modify(serverDBClass);
                }
            }
            originalChildren = entityContext.getChangeTracker().getChildEntityKeys();
        } else
        {
            originalChildren = getChildEntityValueListIncludingDeletedStatusItems(serverDBClass);
        }

        Collection<ITypeFieldValueList> currentChildren = getChildEntityValueListExcludingDeletedStatusItems(
                serverDBClass);
        validateForChildDeletion(serverDBClass, currentChildren);
        Collection<ITypeFieldValueList> deletedChildren = ERDataManagerUtils.findDeletedChildren(originalChildren,
                                                                                                 currentChildren);
        deleteOrphanChildren(con, deletedChildren);
    }

    private void saveForType(ServerDBClass entity, Class type, Connection con) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        if (entityInfo == null)
        {
            return;
        }

        Collection<IDBRelation> dbRelations = entityInfo.getRelations();
        for (IDBRelation relation : dbRelations)
        {
            if (relation.isReverseRelationship())
            {
                continue;
            }
            if (isProxyObject(entity, relation)) continue;
            Collection<ServerDBClass> childObjects = ERDataManagerUtils.getRelationEntities(entity, relation);
            if (childObjects != null)
            {
                if (relation.isNonIdentifyingRelation())
                {
                    for (DBRelationColumnMapping mapping : relation.getTableColumnMappings())
                    {
                        setParentRelationFieldsForNonIdentifyingRelations(entity, relation.getRelatedObjectType(),
                                                                          childObjects, mapping);
                    }
                }
            }
        }

        ITypeFieldValueList fieldValues = ERDataManagerUtils.extractEntityTypeFieldValues(entity, type);
        if (entity.getStatus() == DBClassStatus.UNMODIFIED)
        {
            //do nothing
        } else if (entity.getStatus() == DBClassStatus.NEW)
        {
            insert(entity, fieldValues, type, con);
        } else if (entity.getStatus() == DBClassStatus.MODIFIED)
        {
            if (config.isCheckVersion())
            {
                if (!versionValidated(entity, type, con))
                {
                    throw new DataUpdatedFromAnotherSourceException(String.format(
                            "The type %s updated from another transaction", type));
                }
                ERDataManagerUtils.incrementVersion(fieldValues);
                setValues(entity, fieldValues);
            }
            update(entity, fieldValues, type, con);
        } else if (entity.getStatus() == DBClassStatus.DELETED)
        {
            delete(fieldValues, type, con);
        } else
        {
            String message = String.format("In-corret status for class %s", type.getCanonicalName());
            throw new IncorrectStatusException(message);
        }

        fieldValues = ERDataManagerUtils.extractEntityTypeFieldValues(entity, type);
        IEntityContext entityContext = entity.getContext();
        if (entityContext != null)
        {
            entityContext.getChangeTracker().getFields().clear();
            entityContext.getChangeTracker().getFields().addAll(fieldValues.getFieldValues());
        }

        ERSessionUtils.addToSession(entity, ERDataManagerUtils.extractEntityKeyValues(entity));


        for (IDBRelation relation : dbRelations)
        {
            if (relation.isReverseRelationship()
                    || relation.isNonIdentifyingRelation())
            {
                continue;
            }

            if (isProxyObject(entity, relation)) continue;

            Collection<ServerDBClass> childObjects = ERDataManagerUtils.getRelationEntities(entity, relation);
            if (childObjects != null)
            {
                setRelationObjectKeyValues(fieldValues, type, relation.getRelatedObjectType(), childObjects, relation);
                for (ServerDBClass fieldObject : childObjects)
                {
                    IEntityFieldValueList childEntityKeyList = ERDataManagerUtils.extractEntityKeyValues(fieldObject);
                    if (ERSessionUtils.existsInSession(entity, childEntityKeyList))
                    {
                        continue;
                    }
                    ERSessionUtils.transferSession(entity, fieldObject);
                    if (fieldObject.getStatus() != DBClassStatus.DELETED) //deleted items are already deleted
                    {
                        fieldObject.persist(con);
                        ERSessionUtils.addToSession(entity, childEntityKeyList);
                    }
                }
            }
        }
    }

    private void insert(ServerDBClass entity, ITypeFieldValueList valueTypeList, Class type, Connection con)
    throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        StringBuilder logSb = new StringBuilder();
        String query = entityInfo.getInsertQuery(dbLayer);

        PreparedStatement ps = null;
        try
        {
            ps = con.prepareStatement(query);

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

                    Method keySetter = entityInfo.getSetter(dbColumn);
                    ReflectionUtils.setValue(keySetter, entity, columnValue);
                } else
                {
                    columnValue = fieldValue.getValue();
                }
                if (showQuery)
                {
                    logSb.append(" ,").append(dbColumn.getColumnName()).append("=").append(columnValue);
                }
                dbLayer.getDataManipulate().setToPreparedStatement(ps, columnValue, i + 1, dbColumn);
                i++;
            }
            if (showQuery)
            {
                Logger.getLogger(config.getLoggerName()).info(logSb.toString());
            }
            if (config.isEnableStatistics())
            {
                statistics.registerInsert(type);
            }
            ps.execute();
        } catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying create prepared statement for sql %s", query);
            throw new StatementPreparingException(message, ex);
        } finally
        {
            DBMgmtUtility.close(ps);
        }
    }

    private void update(ServerDBClass entity, ITypeFieldValueList valueTypeList, Class type, Connection con)
    throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        Collection<EntityFieldValue> keys = new ArrayList<EntityFieldValue>();
        Collection<EntityFieldValue> values = new ArrayList<EntityFieldValue>();
        String query;
        StringBuilder logSb = new StringBuilder();

        if (config.isUpdateChangedColumnsOnly())
        {
            values = getModifiedFieldValues(entity, type);
            if (values.size() == 0)
                return;

            keys = ERDataManagerUtils.extractEntityKeyValues(entity).getFieldValues();
            Collection<IDBColumn> keysAndModified = new ArrayList<IDBColumn>();
            for (EntityFieldValue fieldValue : values)
            {
                keysAndModified.add(fieldValue.getDbColumn());
            }
            for (EntityFieldValue fieldValue : keys)
            {
                keysAndModified.add(fieldValue.getDbColumn());
            }
            query = dbLayer.getDataManipulate().createUpdateQuery(entityInfo.getTableName(), keysAndModified);
        } else
        {
            query = entityInfo.getUpdateQuery(dbLayer);
            for (EntityFieldValue fieldValue : valueTypeList.getFieldValues())
            {
                if (fieldValue.getDbColumn().isKey())
                {
                    keys.add(fieldValue);
                } else
                {
                    values.add(fieldValue);
                }
            }
        }

        boolean showQuery = config.isShowQueries();
        if (showQuery)
        {
            logSb.append(query);
        }

        PreparedStatement ps = null;
        try
        {
            ps = con.prepareStatement(query);
            int count = 0;
            for (EntityFieldValue fieldValue : values)
            {
                if (showQuery)
                {
                    logSb.append(" ,").append(fieldValue.getDbColumn().getColumnName()).append("=").append(
                            fieldValue.getValue());
                }
                dbLayer.getDataManipulate().setToPreparedStatement(ps, fieldValue.getValue(), ++count,
                                                                   fieldValue.getDbColumn());
            }

            for (EntityFieldValue fieldValue : keys)
            {
                if (showQuery)
                {
                    logSb.append(" ,").append(fieldValue.getDbColumn().getColumnName()).append("=").append(
                            fieldValue.getValue());
                }
                dbLayer.getDataManipulate().setToPreparedStatement(ps, fieldValue.getValue(), ++count,
                                                                   fieldValue.getDbColumn());
            }
            if (showQuery)
            {
                Logger.getLogger(config.getLoggerName()).info(logSb.toString());
            }
            if (config.isEnableStatistics())
            {
                statistics.registerUpdate(type);
            }
            ps.execute();
        } catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying create prepared statement for sql %s", query);
            throw new StatementPreparingException(message, ex);
        } finally
        {
            DBMgmtUtility.close(ps);
        }
    }

    private void delete(ITypeFieldValueList valueTypeList, Class type, Connection con)
    throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        StringBuilder logSb = new StringBuilder();
        String query = entityInfo.getDeleteQuery(dbLayer);
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

        PreparedStatement ps = null;
        try
        {
            ps = con.prepareStatement(query);
            for (int i = 0; i < keys.size(); i++)
            {
                EntityFieldValue fieldValue = keys.get(i);

                if (showQuery)
                {
                    logSb.append(" ,").append(fieldValue.getDbColumn().getColumnName()).append("=").append(
                            fieldValue.getValue());
                }
                dbLayer.getDataManipulate().setToPreparedStatement(ps, fieldValue.getValue(), i + 1,
                                                                   fieldValue.getDbColumn());
            }
            if (showQuery)
            {
                Logger.getLogger(config.getLoggerName()).info(logSb.toString());
            }
            if (config.isEnableStatistics())
            {
                statistics.registerDelete(type);
            }
            ps.execute();
        } catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying create prepared statement for sql %s", query);
            throw new StatementPreparingException(message, ex);
        } finally
        {
            DBMgmtUtility.close(ps);
        }
    }

    private void setRelationObjectKeyValues(ITypeFieldValueList valueTypeList, Class type, Class childType,
                                            Collection<ServerDBClass> childObjects
            , IDBRelation relation) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        Collection<IDBColumn> columns = entityInfo.getColumns();
        for (DBRelationColumnMapping mapping : relation.getTableColumnMappings())
        {
            IDBColumn matchColumn = ERDataManagerUtils.findColumnByAttribute(columns, mapping.getFromField());
            EntityFieldValue fieldValue = valueTypeList.getFieldValue(matchColumn.getAttributeName());

            if (fieldValue != null)
            {
                setChildPrimaryKeys(fieldValue, childType, childObjects, mapping);
            } else
            {
                String message = String.format("The column %s does not have a matching column in the object %s"
                        , matchColumn.getColumnName(), valueTypeList.getType().getName());
                throw new NoMatchingColumnFoundException(message);
            }
        }
    }

    private void setChildPrimaryKeys(EntityFieldValue parentFieldValue, Class childType
            , Collection<ServerDBClass> childObjects, DBRelationColumnMapping mapping) throws DbGateException
    {
        boolean foundOnce = false;
        EntityInfo parentEntityInfo = CacheManager.getEntityInfo(childType);
        EntityInfo entityInfo = parentEntityInfo;

        while (entityInfo != null)
        {
            Collection<IDBColumn> subLevelColumns = entityInfo.getColumns();
            IDBColumn subLevelMatchedColumn = ERDataManagerUtils.findColumnByAttribute(subLevelColumns,
                                                                                       mapping.getToField());

            if (subLevelMatchedColumn != null)
            {
                foundOnce = true;
                Method setter = parentEntityInfo.getSetter(subLevelMatchedColumn);
                for (ServerRODBClass dbObject : childObjects)
                {
                    ReflectionUtils.setValue(setter, dbObject, parentFieldValue.getValue());
                }
            }
            entityInfo = entityInfo.getSuperEntityInfo();
        }

        if (!foundOnce)
        {
            String message = String.format("The field %s does not have a matching field in the object %s",
                                           mapping.getToField(), childType.getName());
            throw new NoMatchingColumnFoundException(message);
        }
    }

    private void setParentRelationFieldsForNonIdentifyingRelations(ServerDBClass parentEntity, Class childType
            , Collection<ServerDBClass> childObjects, DBRelationColumnMapping mapping) throws DbGateException
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

        EntityInfo parentInfo = CacheManager.getEntityInfo(parentEntity);
        EntityInfo childInfo = CacheManager.getEntityInfo(firstObject);

        Method setter = null;
        boolean foundOnce = false;
        while (parentInfo != null)
        {
            Collection<IDBColumn> parentColumns = parentInfo.getColumns();
            IDBColumn parentMatchedColumn = ERDataManagerUtils.findColumnByAttribute(parentColumns,
                                                                                     mapping.getFromField());
            if (parentMatchedColumn != null)
            {
                foundOnce = true;
                setter = parentInfo.getSetter(parentMatchedColumn);
            }
            parentInfo = parentInfo.getSuperEntityInfo();
        }
        if (!foundOnce)
        {
            String message = String.format("The field %s does not have a matching field in the object %s"
                    , mapping.getToField(), firstObject.getClass().getName());
            throw new NoMatchingColumnFoundException(message);
        }

        foundOnce = false;
        while (childInfo != null)
        {
            Collection<IDBColumn> subLevelColumns = childInfo.getColumns();
            IDBColumn childMatchedColumn = ERDataManagerUtils.findColumnByAttribute(subLevelColumns,
                                                                                    mapping.getToField());

            if (childMatchedColumn != null)
            {
                foundOnce = true;
                for (ServerRODBClass dbObject : childObjects)
                {
                    ITypeFieldValueList fieldValueList = ERDataManagerUtils
                            .extractEntityTypeFieldValues(dbObject, childInfo.getEntityType());
                    EntityFieldValue childFieldValue = fieldValueList
                            .getFieldValue(childMatchedColumn.getAttributeName());
                    ReflectionUtils.setValue(setter, parentEntity, childFieldValue.getValue());
                }
            }
            childInfo = childInfo.getSuperEntityInfo();
        }
        if (!foundOnce)
        {
            String message = String.format("The field %s does not have a matching field in the object %s"
                    , mapping.getToField(), firstObject.getClass().getName());
            throw new NoMatchingColumnFoundException(message);
        }
    }

    private void validateForChildDeletion(ServerDBClass currentEntity, Collection<ITypeFieldValueList> currentChildren)
    throws IntegrityConstraintViolationException
    {
        for (ITypeFieldValueList keyValueList : currentChildren)
        {
            EntityRelationFieldValueList entityRelationKeyValueList = (EntityRelationFieldValueList) keyValueList;
            if (entityRelationKeyValueList.getRelation().getDeleteRule() == ReferentialRuleType.RESTRICT
                    && !entityRelationKeyValueList.getRelation().isReverseRelationship()
                    && currentEntity.getStatus() == DBClassStatus.DELETED)
            {
                throw new IntegrityConstraintViolationException(String.format(
                        "Cannot delete child object %s as restrict constraint in place"
                        , keyValueList.getType().getCanonicalName()));
            }
        }
    }

    private boolean checkForModification(ServerDBClass entity, Connection con, IEntityContext entityContext)
    throws DbGateException
    {
        if (!entityContext.getChangeTracker().isValid())
        {
            fillChangeTrackerValues(entity, con, entityContext);
        }

        EntityInfo entityInfo = CacheManager.getEntityInfo(entity);
        while (entityInfo != null)
        {
            Collection<IDBColumn> subLevelColumns = entityInfo.getColumns();
            for (IDBColumn subLevelColumn : subLevelColumns)
            {
                if (subLevelColumn.isKey())
                {
                    continue;
                }

                Method getter = entityInfo.getGetter(subLevelColumn.getAttributeName());
                Object value = ReflectionUtils.getValue(getter, entity);

                EntityFieldValue fieldValue = entityContext.getChangeTracker().getFieldValue(
                        subLevelColumn.getAttributeName());
                boolean isMatch = (fieldValue != null && fieldValue.getValue() == value)
                        || (fieldValue != null && fieldValue.getValue() != null && fieldValue.equals(value));
                if (!isMatch)
                {
                    return true;
                }
            }
            entityInfo = entityInfo.getSuperEntityInfo();
        }
        return false;
    }

    private void fillChangeTrackerValues(ServerDBClass entity, Connection con, IEntityContext entityContext)
    throws DbGateException
    {
        if (entity.getStatus() == DBClassStatus.NEW
                || entity.getStatus() == DBClassStatus.DELETED)
        {
            return;
        }

        EntityInfo entityInfo = CacheManager.getEntityInfo(entity);
        while (entityInfo != null)
        {
            ITypeFieldValueList values = extractCurrentRowValues(entity, entityInfo.getEntityType(), con);
            for (EntityFieldValue fieldValue : values.getFieldValues())
            {
                entityContext.getChangeTracker().getFields().add(fieldValue);
            }

            Collection<IDBRelation> dbRelations = entityInfo.getRelations();
            for (IDBRelation relation : dbRelations)
            {
                Collection<ServerRODBClass> children = readRelationChildrenFromDb(entity, entityInfo.getEntityType(),
                                                                                  con, relation);
                for (ServerRODBClass childEntity : children)
                {
                    ITypeFieldValueList valueTypeList = ERDataManagerUtils.extractRelationKeyValues(childEntity,
                                                                                                    relation);
                    if (valueTypeList != null)
                    {
                        entityContext.getChangeTracker().getChildEntityKeys().add(valueTypeList);
                    }
                }
            }
            entityInfo = entityInfo.getSuperEntityInfo();
        }
    }

    private void deleteOrphanChildren(Connection con, Collection<ITypeFieldValueList> childrenToDelete)
    throws DbGateException
    {
        for (ITypeFieldValueList relationKeyValueList : childrenToDelete)
        {
            EntityInfo entityInfo = CacheManager.getEntityInfo(relationKeyValueList.getType());
            if (entityInfo == null)
            {
                continue;
            }
            if (relationKeyValueList instanceof EntityRelationFieldValueList)
            {
                EntityRelationFieldValueList relationFieldValueList = (EntityRelationFieldValueList) relationKeyValueList;
                if (relationFieldValueList.getRelation().isNonIdentifyingRelation()
                        || relationFieldValueList.getRelation().isReverseRelationship())
                {
                    continue;
                }
            }

            boolean recordExists = false;
            PreparedStatement ps = createRetrievalPreparedStatement(relationKeyValueList, con);
            ResultSet rs = null;
            try
            {
                rs = ps.executeQuery();
                if (rs.next())
                {
                    recordExists = true;
                }
            } catch (SQLException ex)
            {
                String message = String.format(
                        "SQL Exception while trying determine if orphan child entities are available for type %s"
                        , relationKeyValueList.getType().getCanonicalName());
                throw new StatementExecutionException(message, ex);
            } finally
            {
                DBMgmtUtility.close(rs);
                DBMgmtUtility.close(ps);
            }

            if (recordExists)
            {
                delete(relationKeyValueList, relationKeyValueList.getType(), con);
            }
        }
    }

    private boolean versionValidated(ServerRODBClass entity, Class type, Connection con)
    throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        Collection<IDBColumn> typeColumns = entityInfo.getColumns();
        for (IDBColumn typeColumn : typeColumns)
        {
            if (typeColumn.getColumnType() == DBColumnType.VERSION)
            {
                Object classValue = extractCurrentVersionValue(entity, typeColumn, type, con);
                EntityFieldValue originalFieldValue = entity.getContext().getChangeTracker().getFieldValue(
                        typeColumn.getAttributeName());
                return originalFieldValue != null && classValue == originalFieldValue.getValue()
                        || (originalFieldValue != null && classValue != null && classValue.equals(
                        originalFieldValue.getValue()));
            }
        }

        if (config.isUpdateChangedColumnsOnly())
        {
            Collection<EntityFieldValue> modified = getModifiedFieldValues(entity, type);
            typeColumns = new ArrayList<IDBColumn>();
            for (EntityFieldValue fieldValue : modified)
            {
                typeColumns.add(fieldValue.getDbColumn());
            }
        }

        ITypeFieldValueList fieldValueList = extractCurrentRowValues(entity, type, con);
        if (fieldValueList == null)
        {
            return false;
        }
        for (IDBColumn typeColumn : typeColumns)
        {
            EntityFieldValue classFieldValue = fieldValueList.getFieldValue(typeColumn.getAttributeName());
            EntityFieldValue originalFieldValue = entity.getContext() != null ? entity.getContext().getChangeTracker().getFieldValue(
                    typeColumn.getAttributeName()) : null;
            boolean matches = originalFieldValue != null && classFieldValue != null && classFieldValue.getValue() == originalFieldValue.getValue()
                    || (originalFieldValue != null && classFieldValue != null && classFieldValue.getValue() != null && classFieldValue.getValue().equals(
                    originalFieldValue.getValue()));
            if (!matches)
            {
                return false;
            }
        }
        return true;
    }

    private Collection<EntityFieldValue> getModifiedFieldValues(ServerRODBClass entity, Class type)
    throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        Collection<IDBColumn> typeColumns = entityInfo.getColumns();
        ITypeFieldValueList currentValues = ERDataManagerUtils.extractEntityTypeFieldValues(entity, type);
        Collection<EntityFieldValue> modifiedColumns = new ArrayList<EntityFieldValue>();

        for (IDBColumn typeColumn : typeColumns)
        {
            if (typeColumn.isKey())
                continue;

            EntityFieldValue classFieldValue = currentValues.getFieldValue(typeColumn.getAttributeName());
            EntityFieldValue originalFieldValue = entity.getContext() != null ? entity.getContext().getChangeTracker().getFieldValue(
                    typeColumn.getAttributeName()) : null;
            boolean matches = originalFieldValue != null && classFieldValue != null && classFieldValue.getValue() == originalFieldValue.getValue()
                    || (originalFieldValue != null && classFieldValue != null && classFieldValue.getValue() != null && classFieldValue.getValue().equals(
                    originalFieldValue.getValue()));
            if (!matches)
            {
                modifiedColumns.add(classFieldValue);
            }
        }
        return modifiedColumns;
    }

    private Object extractCurrentVersionValue(ServerRODBClass entity, IDBColumn versionColumn, Class type,
                                              Connection con)
    throws DbGateException
    {
        Object versionValue = null;

        ITypeFieldValueList keyFieldValueList = ERDataManagerUtils.extractEntityTypeKeyValues(entity, type);
        PreparedStatement ps = createRetrievalPreparedStatement(keyFieldValueList, con);
        ResultSet rs = null;
        try
        {
            rs = ps.executeQuery();
            if (rs.next())
            {
                versionValue = dbLayer.getDataManipulate().readFromResultSet(rs, versionColumn);
            }
        } catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying retrieve version information from %s"
                    , entity.getClass().getCanonicalName());
            throw new StatementExecutionException(message, ex);
        } finally
        {
            DBMgmtUtility.close(rs);
            DBMgmtUtility.close(ps);
        }
        return versionValue;
    }

    private ITypeFieldValueList extractCurrentRowValues(ServerRODBClass entity, Class type, Connection con)
    throws DbGateException
    {
        ITypeFieldValueList fieldValueList = null;

        ITypeFieldValueList keyFieldValueList = ERDataManagerUtils.extractEntityTypeKeyValues(entity, type);
        PreparedStatement ps = createRetrievalPreparedStatement(keyFieldValueList, con);
        ResultSet rs = null;
        try
        {
            rs = ps.executeQuery();
            if (rs.next())
            {
                fieldValueList = readValues(type, rs);
            }
        } catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying retrieve current data row from %s"
                    , entity.getClass().getCanonicalName());
            throw new StatementExecutionException(message, ex);
        } finally
        {
            DBMgmtUtility.close(rs);
            DBMgmtUtility.close(ps);
        }
        return fieldValueList;
    }
}
