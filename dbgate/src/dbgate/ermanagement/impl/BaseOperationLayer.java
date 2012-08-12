package dbgate.ermanagement.impl;

import dbgate.*;
import dbgate.utility.DBMgtUtility;
import dbgate.caches.CacheManager;
import dbgate.caches.impl.EntityInfo;
import dbgate.context.EntityFieldValue;
import dbgate.context.IEntityFieldValueList;
import dbgate.context.ITypeFieldValueList;
import dbgate.context.impl.EntityTypeFieldValueList;
import dbgate.exceptions.common.MethodInvocationException;
import dbgate.exceptions.common.NoMatchingColumnFoundException;
import dbgate.exceptions.common.ReadFromResultSetException;
import dbgate.exceptions.common.StatementPreparingException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.utils.OperationUtils;
import dbgate.ermanagement.impl.utils.SessionUtils;
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
public abstract class BaseOperationLayer
{
    protected IDBLayer dbLayer;
    protected IDbGateStatistics statistics;
    protected IDbGateConfig config;

    public BaseOperationLayer(IDBLayer dbLayer, IDbGateStatistics statistics, IDbGateConfig config)
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
        Collection<IColumn> keys = entityInfo.getKeys();

        StringBuilder logSb = new StringBuilder();
        boolean showQuery = config.isShowQueries();
        if (showQuery)
        {
            logSb.append(query);
        }
        int i = 0;
        for (IColumn key : keys)
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
        Collection<IColumn> dbColumns = entityInfo.getColumns();
        for (IColumn dbColumn : dbColumns)
        {
            Object value = dbLayer.getDataManipulate().readFromResultSet(rs,dbColumn);
            valueTypeList.getFieldValues().add(new EntityFieldValue(value,dbColumn));
        }
        return valueTypeList;
    }

    protected static void setValues(IReadOnlyEntity roEntity, ITypeFieldValueList values) throws DbGateException
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

    protected static Collection<ITypeFieldValueList> getChildEntityValueListExcludingDeletedStatusItems(IEntity parentEntity)
        throws DbGateException
    {
        return getChildEntityValueList(parentEntity,false);
    }

    protected static Collection<ITypeFieldValueList> getChildEntityValueListIncludingDeletedStatusItems(IEntity parentEntity)
        throws DbGateException
    {
        return getChildEntityValueList(parentEntity,true);
    }

    protected static Collection<ITypeFieldValueList> getChildEntityValueList(IEntity parentEntity,boolean takeDeleted)
            throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(parentEntity);
        Collection<ITypeFieldValueList> existingEntityChildRelations = new ArrayList<ITypeFieldValueList>();

        while (entityInfo != null)
        {
            Collection<IRelation> typeRelations = entityInfo.getRelations();
            for (IRelation typeRelation : typeRelations)
            {
                if (typeRelation.isReverseRelationship())
                {
                    continue;
                }

                if (isProxyObject(parentEntity,typeRelation))
                {
                    continue;
                }

                Collection<IEntity> childEntities = OperationUtils.getRelationEntities(parentEntity, typeRelation);
                for (IEntity childEntity : childEntities)
                {
                    if (parentEntity.getStatus() == EntityStatus.DELETED
                            && typeRelation.getDeleteRule() == ReferentialRuleType.CASCADE)
                    {
                        childEntity.setStatus(EntityStatus.DELETED);
                    }
                    if (childEntity.getStatus() == EntityStatus.DELETED && !takeDeleted)
                    {
                        continue;
                    }
                    ITypeFieldValueList childKeyValueList =  OperationUtils.extractRelationKeyValues(childEntity,
                                                                                                     typeRelation);
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

    protected static boolean isProxyObject(IEntity entity, IRelation relation) throws DbGateException
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

    protected Collection<IReadOnlyEntity> readRelationChildrenFromDb(IReadOnlyEntity entity,Class type
            ,Connection con,IRelation relation) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        Class childType = relation.getRelatedObjectType();

        StringBuilder logSb = new StringBuilder();
        String query = entityInfo.getRelationObjectLoad(dbLayer,relation);

        ArrayList<String> fields = new ArrayList<String>();
        for (RelationColumnMapping mapping : relation.getTableColumnMappings())
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
        Collection<IColumn> dbColumns = entityInfo.getColumns();
        for (int i = 0; i < fields.size(); i++)
        {
            String field = fields.get(i);
            IColumn matchColumn = OperationUtils.findColumnByAttribute(dbColumns, field);

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

    private Collection<IReadOnlyEntity> executeAndReadFromPreparedStatement(IReadOnlyEntity entity, Connection con,
                                                                            PreparedStatement ps, Class childType)
            throws DbGateException
    {
        ResultSet rs = null;
        EntityInfo entityInfo = CacheManager.getEntityInfo(childType);
        Collection<IColumn> childKeys = null;
        Collection<IReadOnlyEntity> data = new ArrayList<IReadOnlyEntity>();

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
                for (IColumn childKey : childKeys)
                {
                    Object value = dbLayer.getDataManipulate().readFromResultSet(rs,childKey);
                    childTypeKeyList.getFieldValues().add(new EntityFieldValue(value,childKey));
                }
                if (SessionUtils.existsInSession(entity, childTypeKeyList))
                {
                    data.add(SessionUtils.getFromSession(entity, childTypeKeyList));
                    continue;
                }

                IReadOnlyEntity rodbClass = (IReadOnlyEntity) ReflectionUtils.createInstance(childType);
                SessionUtils.transferSession(entity, rodbClass);
                rodbClass.retrieve(rs,con);
                data.add(rodbClass);

                IEntityFieldValueList childEntityKeyList = OperationUtils.extractEntityKeyValues(rodbClass);
                SessionUtils.addToSession(entity, childEntityKeyList);
            }
        }
        catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying to read type %s from result set",childType.getCanonicalName());
            throw new ReadFromResultSetException(message,ex);
        }
        finally
        {
            DBMgtUtility.close(rs);
            DBMgtUtility.close(ps);
        }
        return data;
    }
}
