package dbgate;

import dbgate.ermanagement.impl.DbGate;
import dbgate.support.persistant.changetracker.ChangeTrackerTestOne2ManyEntity;
import dbgate.support.persistant.changetracker.ChangeTrackerTestOne2OneEntity;
import dbgate.support.persistant.changetracker.ChangeTrackerTestRootEntity;
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
public class DbGateChangeTrackerTest
{
    private static DBConnector connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DbGateChangeTrackerTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-change-tracker;create=true");

            String sql = "Create table change_tracker_test_root (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table change_tracker_test_one2many (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tindex_no Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col,index_no))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table change_tracker_test_one2one (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DBConnector("jdbc:derby:memory:unit-testing-change-tracker;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DbGateChangeTrackerTest.class.getName()).severe("Exception during database startup.");
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
    public void changeTracker_changeField_WithAutoTrackChangesOn_shouldUpdateTheEntityInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.setName("Changed-Name");
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity reloadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,reloadedEntity,id);
            connection.close();

            Assert.assertEquals(loadedEntity.getName(),reloadedEntity.getName());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_changeField_WithAutoTrackChangesOnAndClearTracker_shouldUpdateTheEntityInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.getContext().getChangeTracker().getFields().clear();
            loadedEntity.getContext().getChangeTracker().getChildEntityKeys().clear();
            loadedEntity.setName("Changed-Name");
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity reloadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,reloadedEntity,id);
            connection.close();

            Assert.assertEquals(loadedEntity.getName(),reloadedEntity.getName());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_changeField_WithAutoTrackChangesOff_shouldNotUpdateTheEntityInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(false);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.setName("Changed-Name");
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity reloadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,reloadedEntity,id);
            connection.close();

            Assert.assertEquals(entity.getName(),reloadedEntity.getName());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_removeOneToOneChild_WithAutoTrackChangesOn_shouldDeleteChildInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.setOne2OneEntity(new ChangeTrackerTestOne2OneEntity());
            entity.getOne2OneEntity().setName("Child-Org-Name");
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.setOne2OneEntity(null);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToOne = existsOne2OneChild(connection,id);
            connection.close();

            Assert.assertFalse(hasOneToOne);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_removeOneToOneChild_WithAutoTrackChangesOff_DeleteChildInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(false);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.setOne2OneEntity(new ChangeTrackerTestOne2OneEntity());
            entity.getOne2OneEntity().setName("Child-Org-Name");
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.setOne2OneEntity(null);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToOne = existsOne2OneChild(connection,id);
            connection.close();

            Assert.assertFalse(hasOneToOne);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_changeOneToOneChild_WithAutoTrackChangesOn_shouldUpdateChildInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.setOne2OneEntity(new ChangeTrackerTestOne2OneEntity());
            entity.getOne2OneEntity().setName("Child-Org-Name");
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.getOne2OneEntity().setName("Child-Upd-Name");
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity reLoadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            Assert.assertEquals(loadedEntity.getOne2OneEntity().getName(),reLoadedEntity.getOne2OneEntity().getName());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_changeOneToOneChild_WithAutoTrackChangesOff_shouldUpdateChildInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(false);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.setOne2OneEntity(new ChangeTrackerTestOne2OneEntity());
            entity.getOne2OneEntity().setName("Child-Org-Name");
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.getOne2OneEntity().setName("Child-Upd-Name");
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity reLoadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            Assert.assertEquals(entity.getOne2OneEntity().getName(),reLoadedEntity.getOne2OneEntity().getName());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_removeOneToManyChild_WithAutoTrackChangesOn_shouldDeleteChildInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            ChangeTrackerTestOne2ManyEntity one2ManyEntity = new ChangeTrackerTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            entity.getOne2ManyEntities().add(one2ManyEntity);
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.getOne2ManyEntities().clear();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToOne = existsOne2ManyChild(connection,id);
            connection.close();

            Assert.assertFalse(hasOneToOne);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_removeOneToManyChild_WithAutoTrackChangesOff_shouldDeleteChildInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(false);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            ChangeTrackerTestOne2ManyEntity one2ManyEntity = new ChangeTrackerTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            entity.getOne2ManyEntities().add(one2ManyEntity);
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.getOne2ManyEntities().clear();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToOne = existsOne2ManyChild(connection,id);
            connection.close();

            Assert.assertFalse(hasOneToOne);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_changeOneToManyChild_WithAutoTrackChangesOn_shouldUpdateChildInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            ChangeTrackerTestOne2ManyEntity one2ManyEntity = new ChangeTrackerTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            entity.getOne2ManyEntities().add(one2ManyEntity);
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            ChangeTrackerTestOne2ManyEntity loadedOne2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            loadedOne2ManyEntity.setName("Child-Upd-Name");
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity reloadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,reloadedEntity,id);
            ChangeTrackerTestOne2ManyEntity reloadedOne2ManyEntity = reloadedEntity.getOne2ManyEntities().iterator().next();
            connection.close();

            Assert.assertEquals(reloadedOne2ManyEntity.getName(),loadedOne2ManyEntity.getName());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void changeTracker_changeOneToManyChild_WithAutoTrackChangesOff_shouldUpdateChildInDb()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(false);
            Connection connection = connector.getConnection();

            int id = 45;
            ChangeTrackerTestRootEntity entity = new ChangeTrackerTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            ChangeTrackerTestOne2ManyEntity one2ManyEntity = new ChangeTrackerTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            entity.getOne2ManyEntities().add(one2ManyEntity);
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity loadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            ChangeTrackerTestOne2ManyEntity loadedOne2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            loadedOne2ManyEntity.setName("Child-Upd-Name");
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ChangeTrackerTestRootEntity reloadedEntity = new ChangeTrackerTestRootEntity();
            loadEntityWithId(connection,reloadedEntity,id);
            ChangeTrackerTestOne2ManyEntity reloadedOne2ManyEntity = reloadedEntity.getOne2ManyEntities().iterator().next();
            connection.close();

            Assert.assertEquals(reloadedOne2ManyEntity.getName(),one2ManyEntity.getName());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadEntityWithId(Connection connection, ChangeTrackerTestRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from change_tracker_test_root where id_col = ?");
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

    private boolean existsOne2OneChild(Connection connection,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = connection.prepareStatement("select * from change_tracker_test_one2one where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            exists = true;
        }
        rs.close();
        ps.close();

        return exists;
    }

    private boolean existsOne2ManyChild(Connection connection,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = connection.prepareStatement("select * from change_tracker_test_one2many where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            exists = true;
        }
        rs.close();
        ps.close();

        return exists;
    }
    
    @After
    public void afterEach()
    {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-change-tracker;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM change_tracker_test_root");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM change_tracker_test_one2many");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM change_tracker_test_one2one");
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
        Logger.getLogger(DbGateChangeTrackerTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-change-tracker;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-change-tracker").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}
