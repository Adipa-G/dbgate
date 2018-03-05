package dbgate.caches;

import dbgate.*;
import dbgate.caches.impl.EntityInfo;
import dbgate.caches.impl.EntityInfoCache;
import dbgate.exceptions.SequenceGeneratorInitializationException;
import dbgate.exceptions.common.EntityRegistrationException;

import java.util.Collection;
import java.util.List;

/**
 * Date: Mar 24, 2011
 * Time: 10:10:18 PM
 */
public class CacheManager
{
    private static IEntityInfoCache entityInfoCache;

    public static void init(IDbGateConfig config)
    {
        entityInfoCache = new EntityInfoCache(config);
    }

    public static EntityInfo getEntityInfo(Class type)
    {
        return entityInfoCache.getEntityInfo(type);
    }

    public static EntityInfo getEntityInfo(IReadOnlyClientEntity entity)
    {
        return entityInfoCache.getEntityInfo(entity);
    }

    public static Collection<IRelation> GetReversedRelationships(Class entityType)
    {
        return entityInfoCache.GetReversedRelationships(entityType);
    }

    public static void register(Class type) throws SequenceGeneratorInitializationException, EntityRegistrationException
    {
        entityInfoCache.register(type);
    }

    public static void register(Class type,ITable tableInfo,Collection<IField> fields)
    {
        entityInfoCache.register(type,tableInfo,fields);
    }

    public static void clear()
    {
        entityInfoCache.clear();
    }
}
