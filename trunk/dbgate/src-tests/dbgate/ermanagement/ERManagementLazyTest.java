package dbgate.ermanagement;

import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.impl.DbGate;
import dbgate.ermanagement.support.persistant.lazy.LazyOne2ManyEntity;
import dbgate.ermanagement.support.persistant.lazy.LazyOne2OneEntity;
import dbgate.ermanagement.support.persistant.lazy.LazyRootEntity;
import net.sf.cglib.proxy.Enhancer;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class ERManagementLazyTest
{
    private static DBConnector connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(ERManagementLazyTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-lazy;create=true");

            String sql = "Create table lazy_test_root (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table lazy_test_one2many (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tindex_no Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col,index_no))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table lazy_test_one2one (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DBConnector("jdbc:derby:memory:unit-testing-lazy;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);

            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(ERManagementLazyTest.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        if (DBConnector.getSharedInstance() != null)
        {
            DbGate.getSharedInstance().clearCache();
        }
    }

    @Test
    public void lazy_persistAndLoad_WithEmptyLazyFieldsWithLazyOn_shouldHaveProxiesForLazyFields()
    {
        try
        {
            Connection connection = connector.getConnection();
            DbGate.getSharedInstance().getConfig().setEnableStatistics(true);
            DbGate.getSharedInstance().getStatistics().reset();

            int id = 35;
            LazyRootEntity entity = new LazyRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            LazyRootEntity entityReloaded = new LazyRootEntity();
            loadWithColumnEntityWithId(connection,entityReloaded,id);
            connection.commit();
            connection.close();

            boolean isEnhancedOneToMany = Enhancer.isEnhanced(entityReloaded.getOne2ManyEntities().getClass());
            boolean isEnhancedOneToOne = Enhancer.isEnhanced(entityReloaded.getOne2ManyEntities().getClass());
            Assert.assertTrue(isEnhancedOneToMany);
            Assert.assertTrue(isEnhancedOneToOne);
            Assert.assertTrue(DbGate.getSharedInstance().getStatistics().getSelectQueryCount() == 0);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void lazy_persistAndLoad_WithLazyOnWithValuesInLazyFields_shouldRetrieveLazyFieldsInSameConnection()
    {
        try
        {
            Connection connection = connector.getConnection();
            DbGate.getSharedInstance().getConfig().setEnableStatistics(true);
            DbGate.getSharedInstance().getStatistics().reset();

            int id = 35;
            LazyRootEntity entity = new LazyRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            LazyOne2ManyEntity one2Many1 = new LazyOne2ManyEntity();
            one2Many1.setIndexNo(1);
            one2Many1.setName("One2Many1");
            LazyOne2ManyEntity one2Many2 = new LazyOne2ManyEntity();
            one2Many2.setIndexNo(2);
            one2Many2.setName("One2Many2");
            entity.getOne2ManyEntities().add(one2Many1);
            entity.getOne2ManyEntities().add(one2Many2);

            LazyOne2OneEntity one2One = new LazyOne2OneEntity();
            one2One.setName("One2One");
            entity.setOne2OneEntity(one2One);

            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            LazyRootEntity entityReloaded = new LazyRootEntity();
            loadWithColumnEntityWithId(connection,entityReloaded,id);

            Assert.assertTrue(entityReloaded.getOne2ManyEntities().size() == 2);
            Iterator<LazyOne2ManyEntity> iterator = entityReloaded.getOne2ManyEntities().iterator();
            Assert.assertTrue(iterator.next().getName().equals(one2Many1.getName()));
            Assert.assertTrue(iterator.next().getName().equals(one2Many2.getName()));
            Assert.assertTrue(entityReloaded.getOne2OneEntity() != null);
            Assert.assertTrue(entityReloaded.getOne2OneEntity().getName().equals(one2One.getName()));
            Assert.assertTrue(DbGate.getSharedInstance().getStatistics().getSelectQueryCount() == 2);

            connection.commit();
            connection.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void lazy_persistAndLoad_WithLazyOnWithValuesInLazyFields_shouldRetrieveLazyFieldsInAnotherConnection()
    {
        try
        {
            Connection connection = connector.getConnection();
            DbGate.getSharedInstance().getConfig().setEnableStatistics(true);
            DbGate.getSharedInstance().getStatistics().reset();

            int id = 35;
            LazyRootEntity entity = new LazyRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            LazyOne2ManyEntity one2Many1 = new LazyOne2ManyEntity();
            one2Many1.setIndexNo(1);
            one2Many1.setName("One2Many1");
            LazyOne2ManyEntity one2Many2 = new LazyOne2ManyEntity();
            one2Many2.setIndexNo(2);
            one2Many2.setName("One2Many2");
            entity.getOne2ManyEntities().add(one2Many1);
            entity.getOne2ManyEntities().add(one2Many2);

            LazyOne2OneEntity one2One = new LazyOne2OneEntity();
            one2One.setName("One2One");
            entity.setOne2OneEntity(one2One);

            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            LazyRootEntity entityReloaded = new LazyRootEntity();
            loadWithColumnEntityWithId(connection,entityReloaded,id);
            connection.commit();
            connection.close();

            Assert.assertTrue(entityReloaded.getOne2ManyEntities().size() == 2);
            Iterator<LazyOne2ManyEntity> iterator = entityReloaded.getOne2ManyEntities().iterator();
            Assert.assertTrue(iterator.next().getName().equals(one2Many1.getName()));
            Assert.assertTrue(iterator.next().getName().equals(one2Many2.getName()));
            Assert.assertTrue(entityReloaded.getOne2OneEntity() != null);
            Assert.assertTrue(entityReloaded.getOne2OneEntity().getName().equals(one2One.getName()));
            Assert.assertTrue(DbGate.getSharedInstance().getStatistics().getSelectQueryCount() == 2);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void lazy_loadAndPersist_WithLazyOnWithoutFetchingLazyFields_shouldNotLoadLazyLoadingQueries()
    {
        try
        {
            Connection connection = connector.getConnection();
            DbGate.getSharedInstance().getConfig().setEnableStatistics(true);
            DbGate.getSharedInstance().getStatistics().reset();

            int id = 35;
            LazyRootEntity entity = new LazyRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            LazyOne2ManyEntity one2Many1 = new LazyOne2ManyEntity();
            one2Many1.setIndexNo(1);
            one2Many1.setName("One2Many1");
            LazyOne2ManyEntity one2Many2 = new LazyOne2ManyEntity();
            one2Many2.setIndexNo(2);
            one2Many2.setName("One2Many2");
            entity.getOne2ManyEntities().add(one2Many1);
            entity.getOne2ManyEntities().add(one2Many2);

            LazyOne2OneEntity one2One = new LazyOne2OneEntity();
            one2One.setName("One2One");
            entity.setOne2OneEntity(one2One);

            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            LazyRootEntity entityReloaded = new LazyRootEntity();
            loadWithColumnEntityWithId(connection,entityReloaded,id);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            entityReloaded.persist(connection);
            connection.commit();
            connection.close();

            Assert.assertTrue(DbGate.getSharedInstance().getStatistics().getSelectQueryCount() == 0);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean loadWithColumnEntityWithId(Connection connection, LazyRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from lazy_test_root where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadEntity.retrieve(rs,connection);
            loaded = true;
        }
        rs.close();
        ps.close();

        return loaded;
    }

    @After
    public void afterEach()
    {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-lazy;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM lazy_test_root");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM lazy_test_one2many");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM lazy_test_one2one");
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
        Logger.getLogger(ERManagementLazyTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-lazy;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-lazy").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}
