package dbgate.utility;

import dbgate.DBConnector;
import junit.framework.Assert;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Aug 29, 2010
 * Time: 6:40:58 PM
 */
public class DBUtilsTests
{
    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DBUtilsTests.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-dbutility;create=true");
            
            PreparedStatement ps = con.prepareStatement("CREATE TABLE ROOT_ENTITY (ID INT PRIMARY KEY,NAME VARCHAR(12))");
            ps.execute();

            con.commit();
            con.close();

            DBConnector connector = new DBConnector("jdbc:derby:memory:unit-testing-dbutility;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DBUtilsTests.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        try
        {
            Connection con = DBConnector.getSharedInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO ROOT_ENTITY VALUES (10,'TEN'),(20,'TWENTY'),(30,'THIRTY')");
            ps.execute();

            con.commit();
            con.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Test
    public void utils_getConnection_databaseInitialized_shouldCreateConnection()
    {
        try
        {
            Connection connection = DBConnector.getSharedInstance().getConnection();
            Assert.assertTrue(!connection.isClosed());
            connection.close();
        }
        catch (SQLException e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @After
    public void afterEach()
    {
        try
        {
            Connection con = DBConnector.getSharedInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM ROOT_ENTITY");
            ps.execute();

            con.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void after()
    {
        Logger.getLogger(DBUtilsTests.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-dbutility;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-dbutility").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}
