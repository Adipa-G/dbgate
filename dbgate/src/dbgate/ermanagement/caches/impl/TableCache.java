package dbgate.ermanagement.caches.impl;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.caches.ITableCache;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.utils.DBClassAttributeExtractionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 12:57:50 PM
 */
public class TableCache implements ITableCache
{
    private final Map<Class,String> cache = new HashMap<Class, String>();

    @Override
    public String getTableName(Class type) throws TableCacheMissException
    {
        if (cache.containsKey(type))
        {
            return cache.get(type);
        }
        else
        {
            throw new TableCacheMissException(String.format("No cache entry found for %s",type.getCanonicalName()));
        }
    }

    @Override
    public void register(Class type, String tableName)
    {
        if (cache.containsKey(type))
        {
            cache.remove(type);
        }
        cache.put(type,tableName);
    }

    @Override
    public void register(Class type, ServerRODBClass serverRODBClass)
    {
        String tableName = DBClassAttributeExtractionUtils.getTableName(serverRODBClass,type);
        if (cache.containsKey(type))
        {
            return;
        }
        synchronized (cache)
        {
            cache.put(type,tableName);
        }
    }

    @Override
    public void clear()
    {
        cache.clear();
    }
}
