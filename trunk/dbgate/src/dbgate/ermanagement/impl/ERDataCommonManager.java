package dbgate.ermanagement.impl;

import dbgate.DBClassStatus;
import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.context.EntityFieldValue;
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
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Apr 3, 2011
 * Time: 3:38:35 PM
 */
public abstract class ERDataCommonManager
{
    protected IDBLayer dbLayer;
    protected IERLayerConfig config;

    public ERDataCommonManager(IDBLayer dbLayer, IERLayerConfig config)
    {
        this.dbLayer = dbLayer;
        this.config = config;
    }

    protected PreparedStatement createRetrievalPreparedStatement(ITypeFieldValueList keyValueList, Connection con)
            throws TableCacheMissException, QueryBuildingException, FieldCacheMissException, SQLException
    {
        Class targetType = keyValueList.getType();
        String query = CacheManager.queryCache.getLoadQuery(targetType);

        PreparedStatement ps = con.prepareStatement(query);
        Collection<IDBColumn> keys = CacheManager.fieldCache.getKeys(targetType);

        StringBuilder logSb = new StringBuilder();
        boolean showQuery = config.isShowQueries();
        if (showQuery)
        {
            logSb.append(query);
        }
        int i = 0;
        for (IDBColumn key : keys)
        {
            Object fieldValue = keyValueList.getFieldValue(key.getAttributeName()).getValue();
            if (showQuery)
            {
                logSb.append(" ,").append(key.getColumnName()).append("=").append(fieldValue);
            }
            dbLayer.getDataManipulate().setToPreparedStatement(ps,fieldValue,++i,key);
        }
        if (showQuery)
        {
            Logger.getLogger(config.getLoggerName()).info(logSb.toString());
        }
        return ps;
    }

    protected ITypeFieldValueList readValues(Class type, ResultSet rs) throws FieldCacheMissException, SQLException
    {
        ITypeFieldValueList valueTypeList = new EntityTypeFieldValueList(type);
        Collection<IDBColumn> dbColumns = CacheManager.fieldCache.getColumns(type);
        for (IDBColumn dbColumn : dbColumns)
        {
            Object value = dbLayer.getDataManipulate().readFromResultSet(rs,dbColumn);
            valueTypeList.getFieldValues().add(new EntityFieldValue(value,dbColumn));
        }
        return valueTypeList;
    }

    protected static void setValues(ServerRODBClass roEntity, ITypeFieldValueList values) throws FieldCacheMissException
            , NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        for (EntityFieldValue fieldValue : values.getFieldValues())
        {
            Method setter = CacheManager.methodCache.getSetter(roEntity,fieldValue.getDbColumn());
            setter.invoke(roEntity,fieldValue.getValue());
        }
    }

    protected static Collection<ITypeFieldValueList> getChildEntityValueListExcludingDeletedStatusItems(ServerDBClass serverDBClass) throws FieldCacheMissException
            , InvocationTargetException, NoSuchMethodException, IllegalAccessException, SequenceGeneratorInitializationException
            , NoFieldsFoundException
    {
        return getChildEntityValueList(serverDBClass,false);
    }

    protected static Collection<ITypeFieldValueList> getChildEntityValueListIncludingDeletedStatusItems(ServerDBClass serverDBClass) throws FieldCacheMissException
            , InvocationTargetException, NoSuchMethodException, IllegalAccessException, SequenceGeneratorInitializationException
            , NoFieldsFoundException
    {
        return getChildEntityValueList(serverDBClass,true);
    }

    protected static Collection<ITypeFieldValueList> getChildEntityValueList(ServerDBClass parentEntity,boolean takeDeleted) throws FieldCacheMissException
            , InvocationTargetException, NoSuchMethodException, IllegalAccessException, SequenceGeneratorInitializationException
            , NoFieldsFoundException
    {
        Collection<ITypeFieldValueList> existingEntityChildRelations = new ArrayList<ITypeFieldValueList>();

        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(parentEntity.getClass(),new Class[]{ServerDBClass.class});
        for (Class type : typeList)
        {
            Collection<IDBRelation> typeRelations = CacheManager.fieldCache.getRelations(type);
            for (IDBRelation typeRelation : typeRelations)
            {
                if (typeRelation.isReverseRelationship())
                {
                    continue;
                }

                Collection<ServerDBClass> childEntities = ERDataManagerUtils.getRelationEntities(parentEntity,typeRelation);
                for (ServerDBClass childEntity : childEntities)
                {
                    ERDataManagerUtils.registerTypes(childEntity);

                    if (parentEntity.getStatus() == DBClassStatus.DELETED
                            && typeRelation.getDeleteRule() == ReferentialRuleType.CASCADE)
                    {
                        childEntity.setStatus(DBClassStatus.DELETED);
                    }
                    if (childEntity.getStatus() == DBClassStatus.DELETED && !takeDeleted)
                    {
                        continue;
                    }
                    ITypeFieldValueList childKeyValueList =  ERDataManagerUtils.extractRelationKeyValues(childEntity,typeRelation);
                    if (childKeyValueList != null)
                    {
                        existingEntityChildRelations.add(childKeyValueList);
                    }
                }
            }
        }
        return existingEntityChildRelations;
    }

    protected Collection<ServerRODBClass> readRelationChildrenFromDb(ServerRODBClass entity,Class type
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
            if (ERSessionUtils.existsInSession(entity, childTypeKeyList))
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
