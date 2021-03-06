package dbgate.ermanagement.ermapper;

import dbgate.*;
import dbgate.caches.CacheManager;
import dbgate.caches.impl.EntityInfo;
import dbgate.context.IEntityContext;
import dbgate.context.ITypeFieldValueList;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.QueryExecInfo;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.QueryExecParam;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.selection.IAbstractSelection;
import dbgate.ermanagement.ermapper.utils.OperationUtils;
import dbgate.ermanagement.ermapper.utils.ReflectionUtils;
import dbgate.ermanagement.query.IQuerySelection;
import dbgate.exceptions.RetrievalException;
import dbgate.exceptions.common.ReadFromResultSetException;
import dbgate.exceptions.retrival.NoMatchingRecordFoundForSuperClassException;
import dbgate.exceptions.retrival.NoSetterFoundToSetChildObjectListException;
import dbgate.lazy.ChildLoadInterceptor;
import dbgate.utility.DBMgtUtility;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Method;
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
public class RetrievalOperationLayer extends BaseOperationLayer
{
    public RetrievalOperationLayer(IDBLayer dbLayer, IDbGateStatistics statistics, IDbGateConfig config)
    {
        super(dbLayer,statistics,config);
    }

    public Collection select(ISelectionQuery query,ITransaction tx ) throws RetrievalException
    {
        ResultSet rs = null;
        try
        {
            StringBuilder logSb = new StringBuilder();
            boolean showQuery = config.isShowQueries();
            QueryBuildInfo buildInfo = dbLayer.getDataManipulate().processQuery(null, query.getStructure());
            QueryExecInfo execInfo = buildInfo.getExecInfo();
            if (showQuery)
            {
                logSb.append(execInfo.getSql());
                for (QueryExecParam param : execInfo.getParams())
                {
                    logSb.append(" ,").append("Param").append(param.getIndex()).append("=").append(param.getValue());
                }
                Logger.getLogger(config.getLoggerName()).info(logSb.toString());
            }

            rs = dbLayer.getDataManipulate().createResultSet(tx,execInfo);

            Collection<Object> retList = new ArrayList<>();
            Collection<IQuerySelection> selections = query.getStructure().getSelectList();

            while (rs.next())
            {
                int count = 0;
                Object  rowObject = selections.size() > 1 ? new Object[selections.size()] : null;
                for (IQuerySelection selection : selections)
                {
                    Object loaded = ((IAbstractSelection)selection).retrieve(rs,tx,buildInfo);
                    if (selections.size() > 1)
                    {
                        ((Object[])rowObject)[count++] = loaded;
                    }
                    else
                    {
                        rowObject = loaded;
                    }
                }
                retList.add(rowObject);
            }

            return retList;
        }
        catch (Exception e)
        {
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE,e.getMessage(),e);
            throw new RetrievalException(e.getMessage(),e);
        }
        finally
        {
            DBMgtUtility.close(rs);
        }
    }

    public void load(IReadOnlyEntity roEntity, ResultSet rs, ITransaction tx) throws RetrievalException
    {
        if (roEntity instanceof IEntity)
        {
            IEntity entity = (IEntity) roEntity;
            entity.setStatus(EntityStatus.UNMODIFIED);
        }
        try
        {
            loadFromDb(roEntity, rs, tx);
            roEntity.getContext().destroyReferenceStore();
        }
        catch (Exception e)
        {
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE,e.getMessage(),e);
            throw new RetrievalException(e.getMessage(),e);
        }
    }

    private void loadFromDb(IReadOnlyEntity roEntity, ResultSet rs, ITransaction tx) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(roEntity);
        while (entityInfo != null)
        {
            String tableName = entityInfo.getTableInfo().getTableName();
            if (entityInfo.getEntityType() == roEntity.getClass() || tableName == null) //if i==0 that means it's base class and can use existing result set
            {
                loadForType(roEntity, entityInfo.getEntityType(), rs, tx);
            }
            else
            {
                PreparedStatement superPs = null;
                ResultSet superRs = null;
                try
                {
                    ITypeFieldValueList keyValueList = OperationUtils.extractEntityTypeKeyValues(roEntity,
                                                                                                 entityInfo.getEntityType());
                    superPs = createRetrievalPreparedStatement(keyValueList,tx);
                    superRs = superPs.executeQuery();
                    if (superRs.next())
                    {
                        loadForType(roEntity,entityInfo.getEntityType(),superRs,tx);
                    }
                    else
                    {
                        String message = String.format(
                                "Super class %s does not contains a matching record for the base class %s",
                                entityInfo.getEntityType().getCanonicalName(), roEntity.getClass().getCanonicalName());
                        throw new NoMatchingRecordFoundForSuperClassException(message);
                    }
                }
                catch (SQLException ex)
                {
                    String message = String.format("SQL Exception while trying to read from table %s",tableName);
                    throw new ReadFromResultSetException(message,ex);
                }
                finally
                {
                    DBMgtUtility.close(superRs);
                    DBMgtUtility.close(superPs);
                }
            }
            entityInfo = entityInfo.getSuperEntityInfo();
        }
    }

    private void loadForType(IReadOnlyEntity entity,Class type, ResultSet rs, ITransaction tx) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        IEntityContext entityContext = entity.getContext();
        ITypeFieldValueList valueTypeList = readValues(type,rs);
        setValues(entity, valueTypeList);
        entity.getContext().addToCurrentObjectGraphIndex(entity);

        if (entityContext != null)
        {
            entityContext.getChangeTracker().addFields(valueTypeList.getFieldValues());
        }

        Collection<IRelation> dbRelations = entityInfo.getRelations();
        for (IRelation relation : dbRelations)
        {
            loadChildrenFromRelation(entity, type, tx,relation,false);
        }
    }

    public void loadChildrenFromRelation(IReadOnlyEntity parentRoEntity, Class type
            , ITransaction tx, IRelation relation,boolean lazy) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        Method getter = entityInfo.getGetter(relation.getAttributeName());
        Method setter = entityInfo.getSetter(relation.getAttributeName(),new Class[]{getter.getReturnType()});

        if (!lazy && relation.getFetchStrategy() == FetchStrategy.LAZY)
        {
            Class proxyType = getter.getReturnType();
            if (getter.getReturnType().isInterface())
            {
                proxyType = ArrayList.class;
            }
            
            Object proxy = Enhancer.create(proxyType, new ChildLoadInterceptor(this, parentRoEntity, type, tx,
                                                                               relation));
            ReflectionUtils.setValue(setter, parentRoEntity, proxy);
            return;
        }

        IEntityContext entityContext = parentRoEntity.getContext();
        Object value = ReflectionUtils.getValue(getter,parentRoEntity);

        Collection<IReadOnlyEntity> children = readRelationChildrenFromDb(parentRoEntity,type,tx,relation);
        if (entityContext != null
                && !relation.isReverseRelationship())
        {
            for (IReadOnlyEntity childEntity : children)
            {
                ITypeFieldValueList valueTypeList = OperationUtils.extractRelationKeyValues(childEntity, relation);
                if (valueTypeList != null)
                {
                    entityContext.getChangeTracker().addChildEntityKey(valueTypeList);
                }
            }
        }

        if ((value == null || Enhancer.isEnhanced(value.getClass()))
                && ReflectionUtils.isImplementInterface(getter.getReturnType(),Collection.class))
        {
            ReflectionUtils.setValue(setter, parentRoEntity, children);
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
                IReadOnlyEntity singleRODBClass = children.iterator().next();
                if (getter.getReturnType().isAssignableFrom(singleRODBClass.getClass()))
                {
                    ReflectionUtils.setValue(setter, parentRoEntity, singleRODBClass);
                }
                else
                {
                    String message = singleRODBClass.getClass().getName() + " is not matching the getter " + getter.getName();
                    Logger.getLogger(config.getLoggerName()).severe(message);
                    throw new NoSetterFoundToSetChildObjectListException(message);
                }
            }
        }

        if (lazy)
        {
            parentRoEntity.getContext().destroyReferenceStore();
        }
    }
}
