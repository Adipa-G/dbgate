package dbgate.ermanagement;

import dbgate.IEntity;
import dbgate.IReadOnlyEntity;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 11:10:00 PM
 */
public interface IERDataManager
{
    void load(IReadOnlyEntity readOnlyEntity,ResultSet rs,Connection con) throws RetrievalException;

    void save(IEntity entity,Connection con ) throws PersistException;

    Collection select(ISelectionQuery query,Connection con ) throws RetrievalException;

    void clearCache();

    void registerEntity(Class type, String tableName, Collection<IField> fields);
}
