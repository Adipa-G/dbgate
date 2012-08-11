package dbgate.ermanagement.caches;

import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.IField;
import dbgate.ermanagement.exceptions.EntityRegistrationException;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.SequenceGeneratorInitializationException;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 25, 2010
 * Time: 10:54:17 PM
 */
public interface IFieldCache
{
    Collection<IField> getFields(Class type)  throws FieldCacheMissException;

    Collection<IDBColumn> getColumns(Class type)  throws FieldCacheMissException;

    Collection<IDBColumn> getKeys(Class type)  throws FieldCacheMissException;

    Collection<IDBRelation> getRelations(Class type) throws FieldCacheMissException;

    void register(Class type,Collection<IField> fields);

    void register(Class type) throws SequenceGeneratorInitializationException, EntityRegistrationException;

    void clear();
}
