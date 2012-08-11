package dbgate.ermanagement.caches.impl;

import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.ermanagement.IManagedDBClass;
import dbgate.ermanagement.IManagedRODBClass;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.caches.ITableCache;
import dbgate.ermanagement.exceptions.EntityRegistrationException;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.utils.DBClassAttributeExtractionUtils;
import dbgate.ermanagement.impl.utils.ReflectionUtils;

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
            return;
        }
        synchronized (cache)
        {
            cache.put(type,tableName);
        }
    }

    @Override
    public void register(Class type) throws EntityRegistrationException
    {
        if (cache.containsKey(type))
        {
            return;
        }

        IManagedDBClass managedDBClass = null;
        if (ReflectionUtils.isImplementInterface(type,IManagedDBClass.class))
        {
            try
            {
                managedDBClass = (IManagedDBClass)type.newInstance();
            }
            catch (Exception e)
            {
                throw  new EntityRegistrationException(String.format("Could not register type %s",type.getCanonicalName()),e);
            }
        }

        HashMap<Class,String> tempStore = new HashMap<>();
        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(type,new Class[]{ServerRODBClass.class});
        for (Class regType : typeList)
        {
            if (cache.containsKey(regType))
            {
                continue;
            }
            String tableName = managedDBClass != null
                    ? managedDBClass.getTableNames().get(regType)
                    : DBClassAttributeExtractionUtils.getTableName(regType);
            tempStore.put(regType,tableName);
        }

        synchronized (cache)
        {
            for (Class entityType : tempStore.keySet())
            {
                cache.put(entityType,tempStore.get(entityType));
            }
        }
    }

    @Override
    public void clear()
    {
        cache.clear();
    }
}
