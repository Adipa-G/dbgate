package dbgate.ermanagement;

import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.ermanagement.query.*;
import dbgate.ermanagement.support.query.basic.QueryBasicDetailsEntity;
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

            sql = "Create table query_basic_details (\n" +
                    "\tname Varchar(20) NOT NULL,\n" +
                    "\tdescription Varchar(50) NOT NULL )";
            ps = con.prepareStatement(sql);
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
    public void ERQuery_ExecuteToRetrieveAll_WithBasicSqlQuery_shouldLoadAll()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.RawSql("query_basic qb1"))
                    .select(QuerySelection.RawSql("id_col"))
                    .select(QuerySelection.RawSql("name as name_col"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void ERQuery_ExecuteWithCondition_WithBasicSqlQuery_shouldLoadTarget()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.RawSql("query_basic qb1"))
                    .where(QueryCondition.RawSql("id_col = 35"))
                    .where(QueryCondition.RawSql("name like 'Org-NameA'"))
                    .select(QuerySelection.RawSql("id_col,name"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 1);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void ERQuery_ExecuteWithGroup_WithBasicSqlQuery_shouldLoadTarget()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.RawSql("query_basic qb1"))
                    .groupBy(QueryGroup.RawSql("name"))
                    .select(QuerySelection.RawSql("name"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 2);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void ERQuery_ExecuteWithGroupCondition_WithBasicSqlQuery_shouldLoadTarget()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.RawSql("query_basic qb1"))
                    .groupBy(QueryGroup.RawSql("name"))
                    .having(QueryGroupCondition.RawSql("count(id_col)>1"))
                    .select(QuerySelection.RawSql("name"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 1);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void ERQuery_ExecuteWithOrderBy_WithBasicSqlQuery_shouldLoadTarget()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.RawSql("query_basic qb1"))
                    .orderBy(QueryOrderBy.RawSql("name"))
                    .select(QuerySelection.RawSql("name"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void ERQuery_ExecuteWithJoin_WithBasicSqlQuery_shouldLoadTarget()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.RawSql("query_basic qb1"))
                    .join(QueryJoin.RawSql("inner join query_basic_details qbd1 on qb1.name = qbd1.name"))
                    .orderBy(QueryOrderBy.RawSql("qb1.name"))
                    .select(QuerySelection.RawSql("qb1.name as name"))
                    .select(QuerySelection.RawSql("description"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTestData(Connection connection) throws PersistException
    {
        int id = 35;
        QueryBasicEntity entity = new QueryBasicEntity();
        entity.setIdCol(id);
        entity.setName("Org-NameA");
        entity.persist(connection);

        QueryBasicDetailsEntity detailsEntity = new QueryBasicDetailsEntity();
        detailsEntity.setName(entity.getName());
        detailsEntity.setDescription(entity.getName() + "Details");
        detailsEntity.persist(connection);

        id = 45;
        entity = new QueryBasicEntity();
        entity.setIdCol(id);
        entity.setName("Org-NameA");
        entity.persist(connection);

        id = 55;
        entity = new QueryBasicEntity();
        entity.setIdCol(id);
        entity.setName("Org-NameA");
        entity.persist(connection);

        id = 65;
        entity = new QueryBasicEntity();
        entity.setIdCol(id);
        entity.setName("Org-NameB");
        entity.persist(connection);

        detailsEntity = new QueryBasicDetailsEntity();
        detailsEntity.setName(entity.getName());
        detailsEntity.setDescription(entity.getName() + "Details");
        detailsEntity.persist(connection);
    }

    @After
    public void afterEach()
    {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:derby:memory:init-testing-query-basic;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM query_basic");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM query_basic_details");
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
