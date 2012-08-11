package dbgate.ermanagement.caches;

import dbgate.IRODBClass;
import dbgate.ermanagement.IField;
import dbgate.ermanagement.caches.impl.EntityInfo;
import dbgate.ermanagement.exceptions.SequenceGeneratorInitializationException;
import dbgate.ermanagement.exceptions.common.EntityRegistrationException;

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

    EntityInfo getEntityInfo(IRODBClass entity);

    void register(Class type,String tableName,Collection<IField> fields);

    public void register(Class type) throws SequenceGeneratorInitializationException, EntityRegistrationException;

    void clear();
}
