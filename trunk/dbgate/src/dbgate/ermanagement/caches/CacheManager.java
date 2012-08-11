package dbgate.ermanagement.caches;

import dbgate.IRODBClass;
import dbgate.ermanagement.IERLayerConfig;
import dbgate.ermanagement.IField;
import dbgate.ermanagement.caches.impl.EntityInfo;
import dbgate.ermanagement.caches.impl.EntityInfoCache;
import dbgate.ermanagement.exceptions.SequenceGeneratorInitializationException;
import dbgate.ermanagement.exceptions.common.EntityRegistrationException;

import java.util.Collection;

/**
 * Date: Mar 24, 2011
 * Time: 10:10:18 PM
 */
public class CacheManager
{
    private static IEntityInfoCache entityInfoCache;

    public static void init(IERLayerConfig config)
    {
        entityInfoCache = new EntityInfoCache(config);
    }

    public static EntityInfo getEntityInfo(Class type)
    {
        return entityInfoCache.getEntityInfo(type);
    }

    public static EntityInfo getEntityInfo(IRODBClass entity)
    {
        return entityInfoCache.getEntityInfo(entity);
    }

    public static void register(Class type) throws SequenceGeneratorInitializationException, EntityRegistrationException
    {
        entityInfoCache.register(type);
    }

    public static void register(Class type,String tableName,Collection<IField> fields)
    {
        entityInfoCache.register(type,tableName,fields);
    }

    public static void clear()
    {
        entityInfoCache.clear();
    }
}
