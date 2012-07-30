package dbgate.ermanagement;

import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.ermanagement.query.QueryFrom;
import dbgate.ermanagement.query.QuerySelection;
import dbgate.ermanagement.query.SelectionQuery;
import dbgate.ermanagement.support.query.basic.QueryBasicEntity;
import net.sf.cglib.proxy.Enhancer;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class ERManagementQueryBasicTest
{
    private static DBConnector connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(ERManagementQueryBasicTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:init-testing-query-basic;create=true");

            String sql = "Create table query_basic (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DBConnector("jdbc:derby:memory:init-testing-query-basic;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);

            ERLayer.getSharedInstance().getConfig().setAutoTrackChanges(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(ERManagementQueryBasicTest.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        if (DBConnector.getSharedInstance() != null)
        {
            ERLayer.getSharedInstance().clearCache();
        }
    }

    @Test
    public void ERQuery_loadAll_WithBasicSqlQuery_shouldLoadAll()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 35;
            QueryBasicEntity entity = new QueryBasicEntity();
            entity.setIdCol(id);
            entity.setName("Org-NameA");
            entity.persist(connection);

            id = 65;
            entity = new QueryBasicEntity();
            entity.setIdCol(id);
            entity.setName("Org-NameB");
            entity.persist(connection);

            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.RawSql("query_basic qb1"))
                    .select(QuerySelection.RawSql("id_col,name"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 2);
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
            Connection con = DriverManager.getConnection("jdbc:derby:memory:init-testing-query-basic;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM query_basic");
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
        Logger.getLogger(ERManagementQueryBasicTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:init-testing-query-basic;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("init-testing-query-basic").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}
