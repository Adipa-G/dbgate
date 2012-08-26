package dbgate;

import dbgate.exceptions.PersistException;
import dbgate.support.persistant.version.*;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateVersionTest
{
    private static DefaultTransactionFactory connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DbGateVersionTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-version;create=true");

            String sql = "Create table version_test_root (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tversion Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table version_test_one2many (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tversion Int NOT NULL,\n" +
                        "\tindex_no Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col,index_no))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table version_test_one2one (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tversion Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DefaultTransactionFactory("jdbc:derby:memory:unit-testing-version;","org.apache.derby.jdbc.EmbeddedDriver",
                                                      DefaultTransactionFactory.DB_DERBY);

            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            connector.getDbGate().getConfig().setCheckVersion(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DbGateVersionTest.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
        connector.getDbGate().getConfig().setUpdateChangedColumnsOnly(false);
    }

    @Test
    public void version_persistTwice_WithVersionColumnEntity_shouldNotThrowException()
    {
        try
        {
            ITransaction tx = connector.createTransaction();

            int id = 35;
            VersionColumnTestRootEntity entity = new VersionColumnTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            entity.persist(tx);
            tx.commit();
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void version_persistTwice_WithoutVersionColumnEntity_shouldNotThrowException()
    {
        try
        {
            ITransaction tx = connector.createTransaction();

            int id = 45;
            VersionGeneralTestRootEntity entity = new VersionGeneralTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            entity.persist(tx);
            tx.commit();
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(expected = PersistException.class)
    public void version_persistWithTwoChanges_WithoutUpdateChangedColumnsOnly_shouldThrowException() throws Exception
    {
        ITransaction tx = connector.createTransaction();

        int id = 85;
        VersionGeneralTestRootEntity entity = new VersionGeneralTestRootEntity();
        entity.setIdCol(id);
        entity.setName("Org-Name");
        entity.setVersion(1);
        entity.persist(tx);
        tx.commit();
        tx.close();

        tx = connector.createTransaction();
        VersionGeneralTestRootEntity loadedEntityA = new VersionGeneralTestRootEntity();
        VersionGeneralTestRootEntity loadedEntityB = new VersionGeneralTestRootEntity();
        loadWithoutVersionColumnEntityWithId(tx,loadedEntityA, entity.getIdCol());
        loadWithoutVersionColumnEntityWithId(tx,loadedEntityB, entity.getIdCol());
        tx.close();

        tx = connector.createTransaction();
        loadedEntityA.setName("Mod Name");
        loadedEntityA.persist(tx);
        loadedEntityB.setVersion(loadedEntityB.getVersion() + 1);
        loadedEntityB.persist(tx);
        tx.commit();
        tx.close();
    }

    @Test
    public void version_persistWithTwoChanges_WithUpdateChangedColumnsOnly_shouldNotThrowException() throws Exception
    {
        try
        {
            connector.getDbGate().getConfig().setUpdateChangedColumnsOnly(true);
            ITransaction tx = connector.createTransaction();

            int id = 95;
            VersionGeneralTestRootEntity entity = new VersionGeneralTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.setVersion(1);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            VersionGeneralTestRootEntity loadedEntityA = new VersionGeneralTestRootEntity();
            VersionGeneralTestRootEntity loadedEntityB = new VersionGeneralTestRootEntity();
            loadWithoutVersionColumnEntityWithId(tx,loadedEntityA, entity.getIdCol());
            loadWithoutVersionColumnEntityWithId(tx,loadedEntityB, entity.getIdCol());
            tx.close();

            tx = connector.createTransaction();
            loadedEntityA.setName("Mod Name");
            loadedEntityA.persist(tx);
            loadedEntityB.setVersion(loadedEntityB.getVersion() + 1);
            loadedEntityB.persist(tx);
            tx.commit();
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(expected = PersistException.class)
    public void version_rootUpdateFromAnotherTransaction_WithVersionColumnEntity_shouldThrowException() throws Exception
    {
        ITransaction tx;
        VersionColumnTestRootEntity entity = null;
        try
        {
            tx = connector.createTransaction();
            int id = 55;
            entity = new VersionColumnTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            VersionColumnTestRootEntity loadedEntity = new VersionColumnTestRootEntity();
            loadWithVersionColumnEntityWithId(tx, loadedEntity, id);
            loadedEntity.setName("New Name");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

        tx = connector.createTransaction();
        entity.setName("New Name2");
        entity.persist(tx);
        tx.commit();
        tx.close();
    }

    @Test(expected = PersistException.class)
    public void version_rootUpdateFromAnotherTransaction_WithOutVersionColumnEntity_shouldThrowException() throws Exception
    {
        ITransaction tx;
        VersionGeneralTestRootEntity entity = null;

        try
        {
            int id = 65;
            tx = connector.createTransaction();
            entity = new VersionGeneralTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            VersionGeneralTestRootEntity loadedEntity = new VersionGeneralTestRootEntity();
            loadWithoutVersionColumnEntityWithId(tx, loadedEntity, id);
            loadedEntity.setName("New Name");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

        tx = connector.createTransaction();
        entity.setName("New Name2");
        entity.persist(tx);
        tx.commit();
        tx.close();
    }

    @Test(expected = PersistException.class)
    public void version_one2oneChildUpdateFromAnotherTransaction_WithVersionColumnEntity_shouldThrowException() throws Exception
    {
        ITransaction tx;
        VersionColumnTestRootEntity entity = null;

        try
        {
            int id = 55;
            tx = connector.createTransaction();
            entity = new VersionColumnTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            VersionColumnTestOne2OneEntity one2OneEntity = new VersionColumnTestOne2OneEntity();
            one2OneEntity.setName("One2One");
            entity.setOne2OneEntity(one2OneEntity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            VersionColumnTestRootEntity loadedEntity = new VersionColumnTestRootEntity();
            loadWithVersionColumnEntityWithId(tx, loadedEntity, id);
            loadedEntity.getOne2OneEntity().setName("Modified One2One");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

        tx = connector.createTransaction();
        entity.getOne2OneEntity().setName("Modified2 One2One");
        entity.persist(tx);
        tx.commit();
        tx.close();
    }

    @Test(expected = PersistException.class)
    public void version_one2oneChildUpdateFromAnotherTransaction_WithoutVersionColumnEntity_shouldThrowException() throws Exception
    {
        ITransaction tx;
        VersionGeneralTestRootEntity entity = null;

        try
        {
            int id = 55;
            tx = connector.createTransaction();
            entity = new VersionGeneralTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            VersionGeneralTestOne2OneEntity one2OneEntity = new VersionGeneralTestOne2OneEntity();
            one2OneEntity.setName("One2One");
            entity.setOne2OneEntity(one2OneEntity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            VersionGeneralTestRootEntity loadedEntity = new VersionGeneralTestRootEntity();
            loadWithoutVersionColumnEntityWithId(tx, loadedEntity, id);
            loadedEntity.getOne2OneEntity().setName("Modified One2One");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

        tx = connector.createTransaction();
        entity.getOne2OneEntity().setName("Modified2 One2One");
        entity.persist(tx);
        tx.commit();
        tx.close();
    }

    @Test(expected = PersistException.class)
    public void version_one2manyChildUpdateFromAnotherTransaction_WithVersionColumnEntity_shouldThrowException() throws Exception
    {
        ITransaction tx;
        VersionColumnTestRootEntity entity = null;

        try
        {
            int id = 55;
            tx = connector.createTransaction();
            entity = new VersionColumnTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            VersionColumnTestOne2ManyEntity one2ManyEntity = new VersionColumnTestOne2ManyEntity();
            one2ManyEntity.setName("One2Many");
            one2ManyEntity.setIndexNo(1);
            entity.getOne2ManyEntities().add(one2ManyEntity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            VersionColumnTestRootEntity loadedEntity = new VersionColumnTestRootEntity();
            loadWithVersionColumnEntityWithId(tx, loadedEntity, id);
            VersionColumnTestOne2ManyEntity loadedOne2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            loadedOne2ManyEntity.setName("Modified One2Many");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

        tx = connector.createTransaction();
        VersionColumnTestOne2ManyEntity one2ManyEntity = entity.getOne2ManyEntities().iterator().next();
        one2ManyEntity.setName("Modified2 One2Many");
        entity.persist(tx);
        tx.commit();
        tx.close();
    }

    @Test(expected = PersistException.class)
    public void version_one2manyChildUpdateFromAnotherTransaction_WithoutVersionColumnEntity_shouldThrowException() throws Exception
    {
        ITransaction tx;
        VersionGeneralTestRootEntity entity = null;

        try
        {
            int id = 55;
            tx = connector.createTransaction();
            entity = new VersionGeneralTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            VersionGeneralTestOne2ManyEntity one2ManyEntity = new VersionGeneralTestOne2ManyEntity();
            one2ManyEntity.setName("One2Many");
            one2ManyEntity.setIndexNo(1);
            entity.getOne2ManyEntities().add(one2ManyEntity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            VersionGeneralTestRootEntity loadedEntity = new VersionGeneralTestRootEntity();
            loadWithoutVersionColumnEntityWithId(tx, loadedEntity, id);
            VersionGeneralTestOne2ManyEntity loadedOne2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            loadedOne2ManyEntity.setName("Modified One2Many");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

        tx = connector.createTransaction();
        VersionGeneralTestOne2ManyEntity one2ManyEntity = entity.getOne2ManyEntities().iterator().next();
        one2ManyEntity.setName("Modified2 One2Many");
        entity.persist(tx);
        tx.commit();
        tx.close();
    }

    private boolean loadWithVersionColumnEntityWithId(ITransaction tx, VersionColumnTestRootEntity loadEntity,
                                                      int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from version_test_root where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadEntity.retrieve(rs,tx);
            loaded = true;
        }
        rs.close();
        ps.close();

        return loaded;
    }

    private boolean loadWithoutVersionColumnEntityWithId(ITransaction tx, VersionGeneralTestRootEntity loadEntity,
                                                         int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from version_test_root where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadEntity.retrieve(rs,tx);
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
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-version;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM version_test_root");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM version_test_one2many");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM version_test_one2one");
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
        Logger.getLogger(DbGateVersionTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-version;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-version").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}
