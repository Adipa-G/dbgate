package dbgate.ermanagement.impl;

import dbgate.DBClassStatus;
import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.context.IEntityContext;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.exceptions.*;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecInfo;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecParam;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.IAbstractSelection;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.impl.utils.ERSessionUtils;
import dbgate.ermanagement.impl.utils.ReflectionUtils;
import dbgate.ermanagement.lazy.ChildLoadInterceptor;
import net.sf.cglib.proxy.Enhancer;

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
    public ERDataRetrievalManager(IDBLayer dbLayer,IERLayerStatistics statistics, IERLayerConfig config)
    {
        super(dbLayer,statistics,config);
    }

    public Collection select(ISelectionQuery query,Connection con ) throws RetrievalException
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

            rs = dbLayer.getDataManipulate().createResultSet(con,execInfo);

            Collection retList = new ArrayList();
            Collection<IQuerySelection> selections = query.getStructure().getSelectList();

            while (rs.next())
            {
                int count = 0;
                Object[]  rowObjects = new Object[selections.size()];
                for (IQuerySelection selection : selections)
                {
                    Object loaded = ((IAbstractSelection)selection).retrieve(rs,con,buildInfo);
                    rowObjects[count++] = loaded;
                }
                retList.add(rowObjects);
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
            DBMgmtUtility.close(rs);
        }
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
            loadChildrenFromRelation(entity, type, con,relation,false);
        }
    }

    public void loadChildrenFromRelation(ServerRODBClass parentRoEntity, Class type
            , Connection con, IDBRelation relation,boolean lazy) throws NoSuchMethodException, IllegalAccessException
            , InvocationTargetException, TableCacheMissException, QueryBuildingException, SQLException
            , FieldCacheMissException, InstantiationException, NoSetterFoundToSetChildObjectListException
            , RetrievalException, NoMatchingColumnFoundException, NoFieldsFoundException
            , SequenceGeneratorInitializationException
    {
        Method getter = CacheManager.methodCache.getGetter(parentRoEntity,relation.getAttributeName());
        Method setter = CacheManager.methodCache.getSetter(parentRoEntity,relation.getAttributeName(),new Class[]{getter.getReturnType()});

        if (!lazy && relation.isLazy())
        {
            Class proxyType = getter.getReturnType();
            if (getter.getReturnType().isInterface())
            {
                proxyType = ArrayList.class;
            }
            
            Object proxy = Enhancer.create(proxyType, new ChildLoadInterceptor(this, parentRoEntity, type, con,
                                                                               relation));
            setter.invoke(parentRoEntity,proxy);
            return;
        }

        IEntityContext entityContext = parentRoEntity.getContext();
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

        if ((value == null || Enhancer.isEnhanced(value.getClass()))
                && ReflectionUtils.isImplementInterface(getter.getReturnType(),Collection.class))
        {
            setter.invoke(parentRoEntity, children);
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
                    setter.invoke(parentRoEntity,singleRODBClass);
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
}
