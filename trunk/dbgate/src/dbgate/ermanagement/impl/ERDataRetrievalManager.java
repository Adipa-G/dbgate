package dbgate.ermanagement.impl;

import dbgate.DBClassStatus;
import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.DBRelationColumnMapping;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.IERLayerConfig;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.context.EntityFieldValue;
import dbgate.ermanagement.context.IEntityContext;
import dbgate.ermanagement.context.IEntityFieldValueList;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.context.impl.EntityTypeFieldValueList;
import dbgate.ermanagement.exceptions.*;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.impl.utils.ERSessionUtils;
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
 * Time: 3:05:38 PM
 */
public class ERDataRetrievalManager extends ERDataCommonManager
{
    public ERDataRetrievalManager(IDBLayer dbLayer, IERLayerConfig config)
    {
        super(dbLayer,config);
    }

    public void load(ServerRODBClass roEntity, ResultSet rs, Connection con) throws RetrievalException
    {
        if (roEntity instanceof ServerDBClass)
        {
            ServerDBClass entity = (ServerDBClass) roEntity;
            entity.setStatus(DBClassStatus.UNMODIFIED);
        }
        try
        {
            ERSessionUtils.initSession(roEntity);
            ERDataManagerUtils.registerTypes(roEntity);
            loadFromDb(roEntity, rs, con);
            ERSessionUtils.destroySession(roEntity);
        }
        catch (Exception e)
        {
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE,e.getMessage(),e);
            throw new RetrievalException(e.getMessage(),e);
        }
    }

    private void loadFromDb(ServerRODBClass roEntity, ResultSet rs, Connection con) throws TableCacheMissException
            , FieldCacheMissException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
            , SQLException, QueryBuildingException, InstantiationException, NoSetterFoundToSetChildObjectListException
            , RetrievalException, NoMatchingColumnFoundException, NoMatchingRecordFoundForSuperClassException, NoFieldsFoundException
            , SequenceGeneratorInitializationException
    {
        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(roEntity.getClass(),new Class[]{ServerRODBClass.class});
        for (int i = 0, typeListLength = typeList.length; i < typeListLength; i++)
        {
            Class type = typeList[i];
            String tableName = CacheManager.tableCache.getTableName(type);
            if (i == 0 || tableName == null) //if i==0 that means it's base class and can use existing result set
            {
                loadForType(roEntity, type, rs, con);
            }
            else
            {
                PreparedStatement superPs = null;
                ResultSet superRs = null;
                try
                {
                    ITypeFieldValueList keyValueList = ERDataManagerUtils.extractTypeKeyValues(roEntity,type);
                    superPs = createRetrievalPreparedStatement(keyValueList,con);
                    superRs = superPs.executeQuery();
                    if (superRs.next())
                    {
                        loadForType(roEntity,type,superRs,con);
                    }
                    else
                    {
                        String message = String.format("Super class %s does not contains a matching record for the base class %s",type.getCanonicalName(),typeList[0].getCanonicalName());
                        throw new NoMatchingRecordFoundForSuperClassException(message);
                    }
                }
                finally
                {
                    DBMgmtUtility.close(superRs);
                    DBMgmtUtility.close(superPs);
                }
            }
        }
    }

    private void loadForType(ServerRODBClass entity,Class type, ResultSet rs, Connection con) throws FieldCacheMissException
            , InvocationTargetException, NoSuchMethodException, IllegalAccessException, SQLException
            , TableCacheMissException, QueryBuildingException, InstantiationException, NoSetterFoundToSetChildObjectListException
            , RetrievalException, NoMatchingColumnFoundException, SequenceGeneratorInitializationException, NoFieldsFoundException
    {
        IEntityContext entityContext = entity.getContext();
        ITypeFieldValueList valueTypeList = readValues(type,rs);
        setValues(entity, valueTypeList);
        ERSessionUtils.addToSession(entity,ERDataManagerUtils.extractEntityKeyValues(entity));

        if (entityContext != null)
        {
            entityContext.getChangeTracker().getFields().addAll(valueTypeList.getFieldValues());
        }

        Collection<IDBRelation> dbRelations = CacheManager.fieldCache.getRelations(type);
        for (IDBRelation relation : dbRelations)
        {
            loadChildrenFromRelation(entity, type, con,relation);
        }
    }

    private void loadChildrenFromRelation(ServerRODBClass parentRoEntity, Class type, Connection con, IDBRelation relation)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, TableCacheMissException
            , QueryBuildingException, SQLException, FieldCacheMissException, InstantiationException
            , NoSetterFoundToSetChildObjectListException, RetrievalException, NoMatchingColumnFoundException
            , NoFieldsFoundException, SequenceGeneratorInitializationException
    {
        IEntityContext entityContext = parentRoEntity.getContext();

        Method getter = CacheManager.methodCache.getGetter(parentRoEntity,relation.getAttributeName());
        Object value = getter.invoke(parentRoEntity);

        Collection<ServerRODBClass> children = readRelationChildrenFromDb(parentRoEntity,type,con,relation);
        if (entityContext != null
                && !relation.isReverseRelationship())
        {
            for (ServerRODBClass childEntity : children)
            {
                ITypeFieldValueList valueTypeList = ERDataManagerUtils.extractRelationKeyValues(childEntity,relation);
                if (valueTypeList != null)
                {
                    entityContext.getChangeTracker().getChildEntityKeys().add(valueTypeList);
                }
            }
        }

        if (value == null
                && ReflectionUtils.isImplementInterface(getter.getReturnType(),Collection.class))
        {
            Method method = CacheManager.methodCache.getSetter(parentRoEntity,relation.getAttributeName(),new Class[]{getter.getReturnType()});
            method.invoke(parentRoEntity,children);
        }
        else if (value != null
                && ReflectionUtils.isImplementInterface(getter.getReturnType(),Collection.class))
        {
            ((Collection)value).addAll(children);
        }
        else
        {
            if (children.size() > 0)
            {
                ServerRODBClass singleRODBClass = children.iterator().next();
                if (getter.getReturnType().isAssignableFrom(singleRODBClass.getClass()))
                {
                    Method method = CacheManager.methodCache.getSetter(parentRoEntity,relation.getAttributeName(),new Class[]{getter.getReturnType()});
                    method.invoke(parentRoEntity,singleRODBClass);
                }
                else
                {
                    String message = singleRODBClass.getClass().getName() + " is not matching the getter " + getter.getName();
                    Logger.getLogger(config.getLoggerName()).severe(message);
                    throw new NoSetterFoundToSetChildObjectListException(message);
                }
            }
        }
    }

    private Collection<ServerRODBClass> readRelationChildrenFromDb(ServerRODBClass entity,Class type
            ,Connection con,IDBRelation relation) throws TableCacheMissException, QueryBuildingException
            , SQLException, FieldCacheMissException, NoSuchMethodException, InvocationTargetException
            , IllegalAccessException, InstantiationException, RetrievalException, NoMatchingColumnFoundException
            , SequenceGeneratorInitializationException, NoFieldsFoundException
    {
        Class childType = relation.getRelatedObjectType();
        ServerRODBClass childTypeInstance = (ServerRODBClass) childType.newInstance();
        ERDataManagerUtils.registerTypes(childTypeInstance);

        StringBuilder logSb = new StringBuilder();
        String query = CacheManager.queryCache.getRelationObjectLoad(entity.getClass(),relation);

        ArrayList<String> fields = new ArrayList<String>();
        for (DBRelationColumnMapping mapping : relation.getTableColumnMappings())
        {
            fields.add(mapping.getFromField());
        }

        PreparedStatement ps = con.prepareStatement(query);
        boolean showQuery = config.isShowQueries();
        if (showQuery)
        {
            logSb.append(query);
        }
        Collection<IDBColumn> dbColumns = CacheManager.fieldCache.getColumns(type);
        for (int i = 0; i < fields.size(); i++)
        {
            String field = fields.get(i);
            IDBColumn matchColumn = ERDataManagerUtils.findColumnByAttribute(dbColumns, field);

            if (matchColumn != null)
            {
                Method getter = CacheManager.methodCache.getGetter(entity,matchColumn.getAttributeName());
                Object fieldValue = getter.invoke(entity);

                if (showQuery)
                {
                    logSb.append(" ,").append(matchColumn.getColumnName()).append("=").append(fieldValue);
                }
                dbLayer.getDataManipulate().setToPreparedStatement(ps,fieldValue,i+1,matchColumn);
            }
            else
            {
                String message = String.format("The field %s does not have a matching field in the object %s", field,entity.getClass().getName());
                throw new NoMatchingColumnFoundException(message);
            }
        }
        if (showQuery)
        {
            Logger.getLogger(config.getLoggerName()).info(logSb.toString());
        }
        return readFromPreparedStatement(entity, con, ps, childType);
    }

    private Collection<ServerRODBClass> readFromPreparedStatement(ServerRODBClass entity, Connection con, PreparedStatement ps, Class childType)
            throws SQLException, FieldCacheMissException, InstantiationException, IllegalAccessException, RetrievalException, NoSuchMethodException, InvocationTargetException
    {
        Collection<IDBColumn> childKeys = null;
        Collection<ServerRODBClass> data = new ArrayList<ServerRODBClass>();
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            if (childKeys == null)
            {
                childKeys = CacheManager.fieldCache.getKeys(childType);
            }
            ITypeFieldValueList childTypeKeyList = new EntityTypeFieldValueList(childType);
            for (IDBColumn childKey : childKeys)
            {
                Object value = dbLayer.getDataManipulate().readFromResultSet(rs,childKey);
                childTypeKeyList.getFieldValues().add(new EntityFieldValue(value,childKey));
            }
            if (ERSessionUtils.existsInSession(entity,childTypeKeyList))
            {
                data.add(ERSessionUtils.getFromSession(entity,childTypeKeyList));
                continue;
            }

            ServerRODBClass rodbClass = (ServerRODBClass) childType.newInstance();
            ERSessionUtils.transferSession(entity,rodbClass);
            rodbClass.retrieve(rs,con);
            data.add(rodbClass);

            IEntityFieldValueList childEntityKeyList = ERDataManagerUtils.extractEntityKeyValues(rodbClass);
            ERSessionUtils.addToSession(entity,childEntityKeyList);
        }
        DBMgmtUtility.close(rs);
        DBMgmtUtility.close(ps);
        return data;
    }
}
