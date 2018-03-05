package dbgate.utility;

import dbgate.DefaultTransactionFactory;
import dbgate.ITransaction;
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
public class DBUtilsTest
{
    private static DefaultTransactionFactory transactionFactory;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DBUtilsTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-dbutility;create=true");
            
            PreparedStatement ps = con.prepareStatement("CREATE TABLE ROOT_ENTITY (ID INT PRIMARY KEY,NAME VARCHAR(12))");
            ps.execute();

            con.commit();
            con.close();

            transactionFactory = new DefaultTransactionFactory(() -> {
                try {
                    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                    return DriverManager.getConnection("jdbc:derby:memory:unit-testing-dbutility;");
                }
                catch (Exception ex){
                    Logger.getLogger(DBUtilsTest.class.getName()).severe("Exception during database unit-testing-dbutility startup.");
                    return null;
                }
            },DefaultTransactionFactory.DB_DERBY);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DBUtilsTest.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        try
        {
            ITransaction transaction = transactionFactory.createTransaction();
            Connection con = transaction.getConnection();

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
            ITransaction transaction = transactionFactory.createTransaction();
            Connection con = transaction.getConnection();

            Assert.assertTrue(!con.isClosed());
            con.close();
        }
        catch (Exception e)
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
            ITransaction transaction = transactionFactory.createTransaction();
            Connection con = transaction.getConnection();

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
        Logger.getLogger(DBUtilsTest.class.getName()).info("Stopping in-memory database.");
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
