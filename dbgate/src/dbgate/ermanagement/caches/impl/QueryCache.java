package dbgate.ermanagement.caches.impl;

import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.caches.IQueryCache;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.QueryBuildingException;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 6:04:45 PM
 */
public class QueryCache implements IQueryCache
{
    private final static Object lock = new Object();

    private final String INSERT = "INS";
    private final String UPDATE = "UPD";
    private final String DELETE = "DEL";
    private final String LOAD = "LOD";
    private final String COUNT = "COUNT";

    private final static HashMap<String, QueryHolder> queryMap = new HashMap<String, QueryHolder>();
    private IDBLayer dbLayer;

    public QueryCache(IDBLayer dbLayer)
    {
        this.dbLayer = dbLayer;
    }

    private String getQuery(String tableName, Class entityType,String id)
    {
        QueryHolder holder = getHolder(createCacheKey(tableName,entityType));
        return holder.getQuery(id);
    }

    private void setQuery(String tableName, Class entityType,String id,String query)
    {
        QueryHolder holder = getHolder(createCacheKey(tableName,entityType));
        holder.setQuery(id,query);
    }

    private String createCacheKey(String tableName,Class entityType)
    {
        return tableName + "_" + entityType.getCanonicalName();
    }

    private QueryHolder getHolder(String key)
    {
        if (!queryMap.containsKey(key))
        {
            synchronized (lock)
            {
                queryMap.put(key,new QueryHolder());
            }
        }
        return queryMap.get(key);
    }

    @Override
    public String getLoadQuery(Class entityType) throws FieldCacheMissException, QueryBuildingException
            , TableCacheMissException
    {
        String tableName = CacheManager.tableCache.getTableName(entityType);
        String query = getQuery(tableName,entityType,LOAD);
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createLoadQuery(tableName, CacheManager.fieldCache.getColumns(entityType));
            setQuery(tableName,entityType,LOAD,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Load Query building failed for table %s class %s",tableName,entityType.getCanonicalName()));
        }
        return query;
    }

    @Override
    public String getInsertQuery(Class entityType) throws FieldCacheMissException, QueryBuildingException
            , TableCacheMissException
    {
        String tableName = CacheManager.tableCache.getTableName(entityType);
        String query = getQuery(tableName,entityType,INSERT);
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createInsertQuery(tableName,CacheManager.fieldCache.getColumns(entityType));
            setQuery(tableName,entityType,INSERT,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Insert Query building failed for table %s class %s",tableName,entityType.getCanonicalName()));
        }
        return query;
    }

    @Override
    public String getUpdateQuery(Class entityType) throws FieldCacheMissException, QueryBuildingException
            , TableCacheMissException
    {
        String tableName = CacheManager.tableCache.getTableName(entityType);
        String query = getQuery(tableName,entityType,UPDATE);
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createUpdateQuery(tableName,CacheManager.fieldCache.getColumns(entityType));
            setQuery(tableName,entityType,UPDATE,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Update Query building failed for table %s class %s",tableName,entityType.getCanonicalName()));
        }
        return query;
    }

    @Override
    public String getDeleteQuery(Class entityType) throws FieldCacheMissException, QueryBuildingException
            , TableCacheMissException
    {
        String tableName = CacheManager.tableCache.getTableName(entityType);
        String query = getQuery(tableName,entityType,DELETE);
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createDeleteQuery(tableName,CacheManager.fieldCache.getColumns(entityType));
            setQuery(tableName,entityType,DELETE,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Delete Query building failed for table %s class %s",tableName,entityType.getCanonicalName()));
        }
        return query;
    }

    @Override
    public String getRelationObjectLoad(Class entityType, IDBRelation relation) throws QueryBuildingException
            , TableCacheMissException, FieldCacheMissException
    {
        String tableName = CacheManager.tableCache.getTableName(entityType);
        String query = getQuery(tableName,entityType,relation.getRelationshipName());
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createRelatedObjectsLoadQuery(relation);
            setQuery(tableName,entityType,relation.getRelationshipName(),query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Child loading Query building failed for table %s class %s child object type %s",tableName,entityType.getCanonicalName(),relation.getRelatedObjectType().getCanonicalName()));
        }
        return query;
    }

    @Override
    public void clear()
    {
        queryMap.clear();
    }
}
