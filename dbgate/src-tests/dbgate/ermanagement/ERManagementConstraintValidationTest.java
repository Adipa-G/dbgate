package dbgate.ermanagement;

import dbgate.EntityStatus;
import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.impl.DbGate;
import dbgate.ermanagement.support.persistant.constraint.*;
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
public class ERManagementConstraintValidationTest
{
    private static DBConnector connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(ERManagementConstraintValidationTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-constraint;create=true");

            String sql = "Create table constraint_test_root (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table constraint_test_one2many (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tindex_no Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col,index_no))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table constraint_test_one2one (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DBConnector("jdbc:derby:memory:unit-testing-constraint;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);

            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(false);
            DbGate.getSharedInstance().getConfig().setCheckVersion(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(ERManagementConstraintValidationTest.class.getName()).severe("Exception during database startup.");
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
    public void constraintValidation_deleteOneToOneChild_WithReverseRelationShip_shouldNotDeleteChild()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            Connection connection = connector.getConnection();

            int id = 45;
            ConstraintTestReverseRootEntity entity = new ConstraintTestReverseRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);

            ConstraintTestOne2OneEntity one2OneEntity = new ConstraintTestOne2OneEntity();
            one2OneEntity .setIdCol(id);
            one2OneEntity .setName("Child-Org-Name");
            one2OneEntity .persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ConstraintTestReverseRootEntity loadedEntity = new ConstraintTestReverseRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.getOne2OneEntity().setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToOne = existsOne2OneChild(connection,id);
            boolean hasRoot = existsRoot(connection,id);
            connection.close();

            Assert.assertTrue(hasOneToOne);
            Assert.assertTrue(hasRoot);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void constraintValidation_deleteOneToManyChild_WithReverseRelationShip_shouldNotDeleteChild()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 45;
            ConstraintTestReverseRootEntity entity = new ConstraintTestReverseRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);

            ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ConstraintTestReverseRootEntity loadedEntity = new ConstraintTestReverseRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            ConstraintTestOne2ManyEntity loadedOne2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            loadedOne2ManyEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToMany = existsOne2ManyChild(connection,id);
            boolean hasRoot = existsRoot(connection,id);
            connection.close();

            Assert.assertTrue(hasOneToMany);
            Assert.assertTrue(hasRoot);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void constraintValidation_deleteRootWithOneToOneChild_WithReverseRelationShip_shouldNotDeleteChild()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 45;
            ConstraintTestReverseRootEntity entity = new ConstraintTestReverseRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);

            ConstraintTestOne2OneEntity one2OneEntity = new ConstraintTestOne2OneEntity();
            one2OneEntity.setIdCol(id);
            one2OneEntity.setName("Child-Org-Name");
            one2OneEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ConstraintTestReverseRootEntity loadedEntity = new ConstraintTestReverseRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToOne = existsOne2OneChild(connection,id);
            boolean hasRoot = existsRoot(connection,id);
            connection.close();

            Assert.assertTrue(hasOneToOne);
            Assert.assertFalse(hasRoot);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void constraintValidation_deleteRootWithOneToManyChild_WithReverseRelationShip_shouldNotDeleteChild()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 45;
            ConstraintTestReverseRootEntity entity = new ConstraintTestReverseRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);

            ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ConstraintTestReverseRootEntity loadedEntity = new ConstraintTestReverseRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToMany = existsOne2ManyChild(connection,id);
            boolean hasRoot = existsRoot(connection,id);
            connection.close();

            Assert.assertTrue(hasOneToMany);
            Assert.assertFalse(hasRoot);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(expected = PersistException.class)
    public void constraintValidation_deleteRootWithOneToOneChild_WithRestrictConstraint_shouldThrowException() throws Exception
    {
        Connection connection = connector.getConnection();

        int id = 45;
        ConstraintTestDeleteRestrictRootEntity entity = new ConstraintTestDeleteRestrictRootEntity();
        entity.setIdCol(id);
        entity.setName("Org-Name");
        entity.persist(connection);

        ConstraintTestOne2OneEntity one2OneEntity = new ConstraintTestOne2OneEntity();
        one2OneEntity.setIdCol(id);
        one2OneEntity.setName("Child-Org-Name");
        one2OneEntity.persist(connection);
        connection.commit();
        connection.close();

        connection = connector.getConnection();
        ConstraintTestDeleteRestrictRootEntity loadedEntity = new ConstraintTestDeleteRestrictRootEntity();
        loadEntityWithId(connection,loadedEntity,id);
        loadedEntity.setStatus(EntityStatus.DELETED);
        loadedEntity.persist(connection);
        connection.commit();
        connection.close();
    }
    
    @Test(expected = PersistException.class)
    public void constraintValidation_deleteRootWithOneToManyChild_WithRestrictConstraint_shouldThrowException() throws Exception
    {
        Connection connection = connector.getConnection();

        int id = 45;
        ConstraintTestDeleteRestrictRootEntity entity = new ConstraintTestDeleteRestrictRootEntity();
        entity.setIdCol(id);
        entity.setName("Org-Name");
        entity.persist(connection);

        ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
        one2ManyEntity.setIdCol(id);
        one2ManyEntity.setName("Child-Org-Name");
        one2ManyEntity.persist(connection);
        connection.commit();
        connection.close();

        connection = connector.getConnection();
        ConstraintTestDeleteRestrictRootEntity loadedEntity = new ConstraintTestDeleteRestrictRootEntity();
        loadEntityWithId(connection,loadedEntity,id);
        loadedEntity.setStatus(EntityStatus.DELETED);
        loadedEntity.persist(connection);
        connection.commit();
        connection.close();
    }
    
    @Test
    public void constraintValidation_deleteOneToManyChild_WithCascadeConstraint_shouldDeleteChild()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 45;
            ConstraintTestDeleteCascadeRootEntity entity = new ConstraintTestDeleteCascadeRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);

            ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ConstraintTestDeleteCascadeRootEntity loadedEntity = new ConstraintTestDeleteCascadeRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.getOne2ManyEntities().iterator().next().setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToMany = existsOne2ManyChild(connection,id);
            boolean hasRoot = existsRoot(connection,id);
            connection.close();

            Assert.assertFalse(hasOneToMany);
            Assert.assertTrue(hasRoot);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    public void constraintValidation_deleteOneToOneChild_WithCascadeConstraint_shouldDeleteChild()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            Connection connection = connector.getConnection();

            int id = 45;
            ConstraintTestDeleteCascadeRootEntity entity = new ConstraintTestDeleteCascadeRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);

            ConstraintTestOne2OneEntity one2ManyEntity = new ConstraintTestOne2OneEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ConstraintTestDeleteCascadeRootEntity loadedEntity = new ConstraintTestDeleteCascadeRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.getOne2OneEntity().setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToOne = existsOne2ManyChild(connection,id);
            boolean hasRoot = existsRoot(connection,id);
            connection.close();

            Assert.assertFalse(hasOneToOne);
            Assert.assertTrue(hasRoot);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void constraintValidation_deleteOneToManyRoot_WithCascadeConstraint_shouldDeleteBoth()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 45;
            ConstraintTestDeleteCascadeRootEntity entity = new ConstraintTestDeleteCascadeRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);

            ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ConstraintTestDeleteCascadeRootEntity loadedEntity = new ConstraintTestDeleteCascadeRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToMany = existsOne2ManyChild(connection,id);
            boolean hasRoot = existsRoot(connection,id);
            connection.close();

            Assert.assertFalse(hasOneToMany);
            Assert.assertFalse(hasRoot);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void constraintValidation_deleteOneToOneRoot_WithCascadeConstraint_shouldDeleteBoth()
    {
        try
        {
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            Connection connection = connector.getConnection();

            int id = 45;
            ConstraintTestDeleteCascadeRootEntity entity = new ConstraintTestDeleteCascadeRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(connection);

            ConstraintTestOne2OneEntity one2ManyEntity = new ConstraintTestOne2OneEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ConstraintTestDeleteCascadeRootEntity loadedEntity = new ConstraintTestDeleteCascadeRootEntity();
            loadEntityWithId(connection,loadedEntity,id);
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            boolean hasOneToOne = existsOne2ManyChild(connection,id);
            boolean hasRoot = existsRoot(connection,id);
            connection.close();

            Assert.assertFalse(hasOneToOne);
            Assert.assertFalse(hasRoot);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadEntityWithId(Connection connection, ConstraintTestReverseRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from constraint_test_root where id_col = ?");
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
    
    private boolean loadEntityWithId(Connection connection, ConstraintTestDeleteRestrictRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from constraint_test_root where id_col = ?");
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
    
    private boolean loadEntityWithId(Connection connection, ConstraintTestDeleteCascadeRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from constraint_test_root where id_col = ?");
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
    
    private boolean existsRoot(Connection connection,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = connection.prepareStatement("select * from constraint_test_root where id_col = ?");
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

    private boolean existsOne2OneChild(Connection connection,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = connection.prepareStatement("select * from constraint_test_one2one where id_col = ?");
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

        PreparedStatement ps = connection.prepareStatement("select * from constraint_test_one2many where id_col = ?");
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
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-constraint;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM constraint_test_root");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM constraint_test_one2many");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM constraint_test_one2one");
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
        Logger.getLogger(ERManagementConstraintValidationTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-constraint;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-constraint").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}