package dbgate.ermanagement.caches;

import dbgate.ermanagement.caches.impl.FieldCache;
import dbgate.ermanagement.caches.impl.MethodCache;
import dbgate.ermanagement.caches.impl.QueryCache;
import dbgate.ermanagement.caches.impl.TableCache;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;

/**
 * Date: Mar 24, 2011
 * Time: 10:10:18 PM
 */
public class CacheManager
{
    public static IQueryCache queryCache;
    public static IFieldCache fieldCache;
    public static IMethodCache methodCache;
    public static ITableCache tableCache;

    public static void init(IDBLayer dbLayer)
    {
        queryCache = new QueryCache(dbLayer);
        fieldCache = new FieldCache();
        methodCache = new MethodCache();
        tableCache = new TableCache();
    }
}
