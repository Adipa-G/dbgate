package dbgate;

import org.apache.derby.impl.io.VFMemoryStorageFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Date: Mar 29, 2011
 * Time: 11:24:27 PM
 */
public class ExampleBase
{
    protected DBConnector connector;

    public void initializeConnector()
    {
        try
        {
            Logger.getLogger(getClass().getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:testdb;create=true");
            connector = new DBConnector("jdbc:derby:memory:testdb;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);
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
            DriverManager.getConnection("jdbc:derby:memory:testdb;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("testdb").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
        connector.finalize();
        connector = null;
    }
}
