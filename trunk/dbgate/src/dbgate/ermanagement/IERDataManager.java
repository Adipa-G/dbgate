package dbgate.ermanagement;

import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
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
    void load(ServerRODBClass serverRODBClass,ResultSet rs,Connection con) throws RetrievalException;

    void save(ServerDBClass serverDBClass,Connection con ) throws PersistException;

    void clearCache();

    void registerTable(Class type,String tableName);

    void registerFields(Class type,Collection<IField> fields);
}