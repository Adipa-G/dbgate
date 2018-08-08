package dbgate;

import docgenerate.WikiCodeBlock;
import org.apache.derby.impl.io.VFMemoryStorageFactory;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Date: Mar 29, 2011
 * Time: 11:24:27 PM
 */
@WikiCodeBlock(id = "example_base")
public class ExampleBase
{
    protected ITransactionFactory factory;
    protected String dbName = "testdb";

    public void initializeConnector()
    {
        try
        {
            factory = new DefaultTransactionFactory(() -> {
                try {
                    Logger.getLogger(getClass().getName()).info("Starting in-memory database for unit tests");
                    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                    return DriverManager.getConnection(String.format("jdbc:derby:memory:%s;create=true", dbName));
                }
                catch (Exception ex){
                    Logger.getLogger(getClass().getName()).severe(String.format("Exception during database %s startup.",dbName));
                    return null;
                }
            }, DefaultTransactionFactory.DB_DERBY);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(getClass().getName()).severe("Exception during database startup.");
        }
    }

    public void destroyConnector()
    {
        try
        {
            DriverManager.getConnection(String.format("jdbc:derby:memory:%s;shutdown=true",dbName)).close();
        }
        catch (SQLException ex)
        {
            if (ex.getErrorCode() != 45000)
            {
                ex.printStackTrace();
            }
        }
        try
        {
            VFMemoryStorageFactory.purgeDatabase(new File(dbName).getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
        ((DefaultTransactionFactory)factory).finalize();
        factory = null;
    }
}

