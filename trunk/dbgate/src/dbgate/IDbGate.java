package dbgate;

import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 11:08:29 PM
 */
public interface IDbGate
{
    void load(IReadOnlyEntity readOnlyEntity, ResultSet rs, Connection con) throws RetrievalException;

    void save(IEntity entity, Connection con) throws PersistException;

    Collection select(ISelectionQuery query,Connection con ) throws RetrievalException;

    void patchDataBase(Connection con, Collection<Class> entityTypes, boolean dropAll) throws DBPatchingException;

    void clearCache();

    void registerEntity(Class type,String tableName,Collection<IField> fields);

    IDbGateConfig getConfig();

    IDbGateStatistics getStatistics();
}
