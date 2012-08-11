package dbgate.ermanagement.impl;

import dbgate.DBClassStatus;
import dbgate.DbGateException;
import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.caches.impl.EntityInfo;
import dbgate.ermanagement.context.EntityFieldValue;
import dbgate.ermanagement.context.IEntityFieldValueList;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.context.impl.EntityTypeFieldValueList;
import dbgate.ermanagement.exceptions.common.MethodInvocationException;
import dbgate.ermanagement.exceptions.common.NoMatchingColumnFoundException;
import dbgate.ermanagement.exceptions.common.ReadFromResultSetException;
import dbgate.ermanagement.exceptions.common.StatementPreparingException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.impl.utils.ERSessionUtils;
import dbgate.ermanagement.impl.utils.ReflectionUtils;
import net.sf.cglib.proxy.Enhancer;

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
    protected IERLayerStatistics statistics;
    protected IERLayerConfig config;

    public ERDataCommonManager(IDBLayer dbLayer,IERLayerStatistics statistics, IERLayerConfig config)
    {
        this.dbLayer = dbLayer;
        this.statistics = statistics;
        this.config = config;
    }

    protected PreparedStatement createRetrievalPreparedStatement(ITypeFieldValueList keyValueList, Connection con)
            throws DbGateException
    {
        Class targetType = keyValueList.getType();
        EntityInfo entityInfo = CacheManager.getEntityInfo(targetType);

        String query = entityInfo.getLoadQuery(dbLayer);
        PreparedStatement ps;
        try
        {
            ps = con.prepareStatement(query);
        }
        catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying create prepared statement for sql %s",query);
            throw new StatementPreparingException(message,ex);
        }
        Collection<IDBColumn> keys = entityInfo.getKeys();

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
        if (config.isEnableStatistics())
        {
            statistics.registerSelect(targetType);
        }

        return ps;
    }

    protected ITypeFieldValueList readValues(Class type, ResultSet rs) throws ReadFromResultSetException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);

        ITypeFieldValueList valueTypeList = new EntityTypeFieldValueList(type);
        Collection<IDBColumn> dbColumns = entityInfo.getColumns();
        for (IDBColumn dbColumn : dbColumns)
        {
            Object value = dbLayer.getDataManipulate().readFromResultSet(rs,dbColumn);
            valueTypeList.getFieldValues().add(new EntityFieldValue(value,dbColumn));
        }
        return valueTypeList;
    }

    protected static void setValues(ServerRODBClass roEntity, ITypeFieldValueList values) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(roEntity);

        try
        {
            for (EntityFieldValue fieldValue : values.getFieldValues())
            {
                Method setter = entityInfo.getSetter(fieldValue.getDbColumn());
                setter.invoke(roEntity,fieldValue.getValue());
            }
        }
        catch (Exception ex)
        {
            String message = String.format("Exception while trying to invoking setters of entity %s",entityInfo.getEntityType().getCanonicalName());
            throw new MethodInvocationException(message,ex);
        }
    }

    protected static Collection<ITypeFieldValueList> getChildEntityValueListExcludingDeletedStatusItems(ServerDBClass parentEntity)
        throws DbGateException
    {
        return getChildEntityValueList(parentEntity,false);
    }

    protected static Collection<ITypeFieldValueList> getChildEntityValueListIncludingDeletedStatusItems(ServerDBClass parentEntity)
        throws DbGateException
    {
        return getChildEntityValueList(parentEntity,true);
    }

    protected static Collection<ITypeFieldValueList> getChildEntityValueList(ServerDBClass parentEntity,boolean takeDeleted)
            throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(parentEntity);
        Collection<ITypeFieldValueList> existingEntityChildRelations = new ArrayList<ITypeFieldValueList>();

        while (entityInfo != null)
        {
            Collection<IDBRelation> typeRelations = entityInfo.getRelations();
            for (IDBRelation typeRelation : typeRelations)
            {
                if (typeRelation.isReverseRelationship())
                {
                    continue;
                }

                if (isProxyObject(parentEntity,typeRelation))
                {
                    continue;
                }

                Collection<ServerDBClass> childEntities = ERDataManagerUtils.getRelationEntities(parentEntity,typeRelation);
                for (ServerDBClass childEntity : childEntities)
                {
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
            entityInfo = entityInfo.getSuperEntityInfo();
        }

        return existingEntityChildRelations;
    }

    protected static boolean isProxyObject(ServerDBClass entity, IDBRelation relation) throws DbGateException
    {
        if (relation.isLazy())
        {
            EntityInfo entityInfo = CacheManager.getEntityInfo(entity);
            Object value;
            try
            {
                Method getter = entityInfo.getGetter(relation.getAttributeName());
                value = getter.invoke(entity);
            }
            catch (Exception ex)
            {
                String message = String.format("Exception while trying to invoking setters of entity %s",entity.getClass().getCanonicalName());
                throw new MethodInvocationException(message,ex);
            }

            if (value == null)
            {
                return false;
            }

            if (Enhancer.isEnhanced(value.getClass()))
            {
                return true;
            }
        }
        return false;
    }

    protected Collection<ServerRODBClass> readRelationChildrenFromDb(ServerRODBClass entity,Class type
            ,Connection con,IDBRelation relation) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        Class childType = relation.getRelatedObjectType();

        StringBuilder logSb = new StringBuilder();
        String query = entityInfo.getRelationObjectLoad(dbLayer,relation);

        ArrayList<String> fields = new ArrayList<String>();
        for (DBRelationColumnMapping mapping : relation.getTableColumnMappings())
        {
            fields.add(mapping.getFromField());
        }

        PreparedStatement ps;
        try
        {
            ps = con.prepareStatement(query);
        }
        catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying create prepared statement for sql %s",query);
            throw new StatementPreparingException(message,ex);
        }

        boolean showQuery = config.isShowQueries();
        if (showQuery)
        {
            logSb.append(query);
        }
        Collection<IDBColumn> dbColumns = entityInfo.getColumns();
        for (int i = 0; i < fields.size(); i++)
        {
            String field = fields.get(i);
            IDBColumn matchColumn = ERDataManagerUtils.findColumnByAttribute(dbColumns, field);

            if (matchColumn != null)
            {
                Method getter = entityInfo.getGetter(matchColumn.getAttributeName());
                Object fieldValue = ReflectionUtils.getValue(getter,entity);

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
        if (config.isEnableStatistics())
        {
            statistics.registerSelect(childType);
        }
        return executeAndReadFromPreparedStatement(entity, con, ps, childType);
    }

    private Collection<ServerRODBClass> executeAndReadFromPreparedStatement(ServerRODBClass entity, Connection con,
                                                                            PreparedStatement ps, Class childType)
            throws DbGateException
    {
        ResultSet rs = null;
        EntityInfo entityInfo = CacheManager.getEntityInfo(childType);
        Collection<IDBColumn> childKeys = null;
        Collection<ServerRODBClass> data = new ArrayList<ServerRODBClass>();

        try
        {
            rs = ps.executeQuery();
            while (rs.next())
            {
                if (childKeys == null)
                {
                    childKeys = entityInfo.getKeys();
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

                ServerRODBClass rodbClass = (ServerRODBClass) ReflectionUtils.createInstance(childType);
                ERSessionUtils.transferSession(entity,rodbClass);
                rodbClass.retrieve(rs,con);
                data.add(rodbClass);

                IEntityFieldValueList childEntityKeyList = ERDataManagerUtils.extractEntityKeyValues(rodbClass);
                ERSessionUtils.addToSession(entity,childEntityKeyList);
            }
        }
        catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying to read type %s from result set",childType.getCanonicalName());
            throw new ReadFromResultSetException(message,ex);
        }
        finally
        {
            DBMgmtUtility.close(rs);
            DBMgmtUtility.close(ps);
        }
        return data;
    }
}
