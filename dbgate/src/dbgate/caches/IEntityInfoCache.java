package dbgate.caches;

import dbgate.IField;
import dbgate.IReadOnlyClientEntity;
import dbgate.IRelation;
import dbgate.ITable;
import dbgate.caches.impl.EntityInfo;
import dbgate.exceptions.SequenceGeneratorInitializationException;
import dbgate.exceptions.common.EntityRegistrationException;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 25, 2010
 * Time: 10:54:17 PM
 */
public interface IEntityInfoCache
{
    EntityInfo getEntityInfo(Class type);

    EntityInfo getEntityInfo(IReadOnlyClientEntity entity);

    Collection<IRelation> GetReversedRelationships(Class entityType);

    void register(Class type,ITable tableInfo,Collection<IField> fields);

    public void register(Class type) throws SequenceGeneratorInitializationException, EntityRegistrationException;

    void clear();
}
