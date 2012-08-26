package dbgate;

import dbgate.exceptions.PersistException;
import dbgate.support.persistant.constraint.*;
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
public class DbGateConstraintValidationTest
{
    private static DefaultTransactionFactory connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DbGateConstraintValidationTest.class.getName()).info("Starting in-memory database for unit tests");
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

            connector = new DefaultTransactionFactory("jdbc:derby:memory:unit-testing-constraint;","org.apache.derby.jdbc.EmbeddedDriver",
                                                      DefaultTransactionFactory.DB_DERBY);

            connector.getDbGate().getConfig().setAutoTrackChanges(false);
            connector.getDbGate().getConfig().setCheckVersion(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DbGateConstraintValidationTest.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
    }

    @Test
    public void constraintValidation_deleteOneToOneChild_WithReverseRelationShip_shouldNotDeleteChild()
    {
        try
        {
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            ConstraintTestReverseRootEntity entity = new ConstraintTestReverseRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);

            ConstraintTestOne2OneEntity one2OneEntity = new ConstraintTestOne2OneEntity();
            one2OneEntity .setIdCol(id);
            one2OneEntity .setName("Child-Org-Name");
            one2OneEntity .persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ConstraintTestReverseRootEntity loadedEntity = new ConstraintTestReverseRootEntity();
            loadEntityWithId(tx,loadedEntity, id);
            loadedEntity.getOne2OneEntity().setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToOne = existsOne2OneChild(tx,id);
            boolean hasRoot = existsRoot(tx,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = 45;
            ConstraintTestReverseRootEntity entity = new ConstraintTestReverseRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);

            ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ConstraintTestReverseRootEntity loadedEntity = new ConstraintTestReverseRootEntity();
            loadEntityWithId(tx,loadedEntity,id);
            ConstraintTestOne2ManyEntity loadedOne2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            loadedOne2ManyEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToMany = existsOne2ManyChild(tx,id);
            boolean hasRoot = existsRoot(tx,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = 45;
            ConstraintTestReverseRootEntity entity = new ConstraintTestReverseRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);

            ConstraintTestOne2OneEntity one2OneEntity = new ConstraintTestOne2OneEntity();
            one2OneEntity.setIdCol(id);
            one2OneEntity.setName("Child-Org-Name");
            one2OneEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ConstraintTestReverseRootEntity loadedEntity = new ConstraintTestReverseRootEntity();
            loadEntityWithId(tx, loadedEntity,id);
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToOne = existsOne2OneChild(tx,id);
            boolean hasRoot = existsRoot(tx,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = 45;
            ConstraintTestReverseRootEntity entity = new ConstraintTestReverseRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);

            ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ConstraintTestReverseRootEntity loadedEntity = new ConstraintTestReverseRootEntity();
            loadEntityWithId(tx, loadedEntity,id);
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToMany = existsOne2ManyChild(tx,id);
            boolean hasRoot = existsRoot(tx,id);
            tx.close();

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
        ITransaction tx = connector.createTransaction();

        int id = 45;
        ConstraintTestDeleteRestrictRootEntity entity = new ConstraintTestDeleteRestrictRootEntity();
        entity.setIdCol(id);
        entity.setName("Org-Name");
        entity.persist(tx);

        ConstraintTestOne2OneEntity one2OneEntity = new ConstraintTestOne2OneEntity();
        one2OneEntity.setIdCol(id);
        one2OneEntity.setName("Child-Org-Name");
        one2OneEntity.persist(tx);
        tx.commit();
        tx.close();

        tx = connector.createTransaction();
        ConstraintTestDeleteRestrictRootEntity loadedEntity = new ConstraintTestDeleteRestrictRootEntity();
        loadEntityWithId(tx,loadedEntity,id);
        loadedEntity.setStatus(EntityStatus.DELETED);
        loadedEntity.persist(tx);
        tx.commit();
        tx.close();
    }
    
    @Test(expected = PersistException.class)
    public void constraintValidation_deleteRootWithOneToManyChild_WithRestrictConstraint_shouldThrowException() throws Exception
    {
        ITransaction tx = connector.createTransaction();

        int id = 45;
        ConstraintTestDeleteRestrictRootEntity entity = new ConstraintTestDeleteRestrictRootEntity();
        entity.setIdCol(id);
        entity.setName("Org-Name");
        entity.persist(tx);

        ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
        one2ManyEntity.setIdCol(id);
        one2ManyEntity.setName("Child-Org-Name");
        one2ManyEntity.persist(tx);
        tx.commit();
        tx.close();

        tx = connector.createTransaction();
        ConstraintTestDeleteRestrictRootEntity loadedEntity = new ConstraintTestDeleteRestrictRootEntity();
        loadEntityWithId(tx,loadedEntity,id);
        loadedEntity.setStatus(EntityStatus.DELETED);
        loadedEntity.persist(tx);
        tx.commit();
        tx.close();
    }
    
    @Test
    public void constraintValidation_deleteOneToManyChild_WithCascadeConstraint_shouldDeleteChild()
    {
        try
        {
            ITransaction tx = connector.createTransaction();

            int id = 45;
            ConstraintTestDeleteCascadeRootEntity entity = new ConstraintTestDeleteCascadeRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);

            ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ConstraintTestDeleteCascadeRootEntity loadedEntity = new ConstraintTestDeleteCascadeRootEntity();
            loadEntityWithId(tx,loadedEntity,id);
            loadedEntity.getOne2ManyEntities().iterator().next().setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToMany = existsOne2ManyChild(tx,id);
            boolean hasRoot = existsRoot(tx,id);
            tx.close();

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
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            ConstraintTestDeleteCascadeRootEntity entity = new ConstraintTestDeleteCascadeRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);

            ConstraintTestOne2OneEntity one2ManyEntity = new ConstraintTestOne2OneEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ConstraintTestDeleteCascadeRootEntity loadedEntity = new ConstraintTestDeleteCascadeRootEntity();
            loadEntityWithId(tx,loadedEntity, id);
            loadedEntity.getOne2OneEntity().setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToOne = existsOne2ManyChild(tx,id);
            boolean hasRoot = existsRoot(tx,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = 45;
            ConstraintTestDeleteCascadeRootEntity entity = new ConstraintTestDeleteCascadeRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);

            ConstraintTestOne2ManyEntity one2ManyEntity = new ConstraintTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ConstraintTestDeleteCascadeRootEntity loadedEntity = new ConstraintTestDeleteCascadeRootEntity();
            loadEntityWithId(tx, loadedEntity,id);
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToMany = existsOne2ManyChild(tx,id);
            boolean hasRoot = existsRoot(tx,id);
            tx.close();

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
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            ConstraintTestDeleteCascadeRootEntity entity = new ConstraintTestDeleteCascadeRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);

            ConstraintTestOne2OneEntity one2ManyEntity = new ConstraintTestOne2OneEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setName("Child-Org-Name");
            one2ManyEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ConstraintTestDeleteCascadeRootEntity loadedEntity = new ConstraintTestDeleteCascadeRootEntity();
            loadEntityWithId(tx, loadedEntity,id);
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToOne = existsOne2ManyChild(tx,id);
            boolean hasRoot = existsRoot(tx,id);
            tx.close();

            Assert.assertFalse(hasOneToOne);
            Assert.assertFalse(hasRoot);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadEntityWithId(ITransaction tx, ConstraintTestReverseRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from constraint_test_root where id_col = ?");
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
    
    private boolean loadEntityWithId(ITransaction tx, ConstraintTestDeleteRestrictRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from constraint_test_root where id_col = ?");
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
    
    private boolean loadEntityWithId(ITransaction tx, ConstraintTestDeleteCascadeRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from constraint_test_root where id_col = ?");
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
    
    private boolean existsRoot(ITransaction tx,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from constraint_test_root where id_col = ?");
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

    private boolean existsOne2OneChild(ITransaction tx,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from constraint_test_one2one where id_col = ?");
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

    private boolean existsOne2ManyChild(ITransaction tx,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from constraint_test_one2many where id_col = ?");
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
        Logger.getLogger(DbGateConstraintValidationTest.class.getName()).info("Stopping in-memory database.");
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