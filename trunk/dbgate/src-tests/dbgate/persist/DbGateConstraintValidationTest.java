package dbgate.persist;

import dbgate.*;
import dbgate.exceptions.PersistException;
import dbgate.persist.support.constraint.*;
import org.junit.*;

import java.sql.*;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateConstraintValidationTest extends AbstractDbGateTestBase
{
    private static final String dbName = "unit-testing-constraint";

    @BeforeClass
    public static void before()
    {
        testClass = DbGateConstraintValidationTest.class;
        beginInit(dbName);

        String sql = "Create table constraint_test_root (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        sql = "Create table constraint_test_one2many (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tindex_no Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col,index_no))";
        createTableFromSql(sql,dbName);

        sql = "Create table constraint_test_one2one (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        endInit(dbName);
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
        connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.MANUAL);
        connector.getDbGate().getConfig().setDefaultVerifyOnWriteStrategy(VerifyOnWriteStrategy.DO_NOT_VERIFY);
    }

    @Test
    public void constraintValidation_deleteOneToOneChild_WithReverseRelationShip_shouldNotDeleteChild()
    {
        try
        {
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
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
        cleanupDb(dbName);
    }

    @AfterClass
    public static void after()
    {
        finalizeDb(dbName);
    }
}