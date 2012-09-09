package dbgate;

import dbgate.context.impl.ChangeTracker;
import dbgate.ermanagement.ermapper.utils.ReflectionUtils;
import dbgate.support.persistant.dirtycheck.DirtyCheckTestOne2ManyEntity;
import dbgate.support.persistant.dirtycheck.DirtyCheckTestOne2OneEntity;
import dbgate.support.persistant.dirtycheck.DirtyCheckTestRootEntity;
import org.junit.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Collection;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateDirtyCheckTest extends AbstractDbGateTestBase
{
    private static final String dbName = "unit-testing-dirty-check";

    @BeforeClass
    public static void before()
    {
        testClass = DbGateDirtyCheckTest.class;
        beginInit(dbName);

        String sql = "Create table dirty_check_test_root (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        sql = "Create table dirty_check_test_one2many (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tindex_no Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col,index_no))";
        createTableFromSql(sql,dbName);

        sql = "Create table dirty_check_test_one2one (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        endInit(dbName);
        connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.MANUAL);
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
    }

    @Test
    public void changeTracker_changeField_WithAutoTrackChangesOn_shouldUpdateTheEntityInDb()
    {
        try
        {
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, loadedEntity,id);
            loadedEntity.setName("Changed-Name");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity reloadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, reloadedEntity, id);
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx,loadedEntity,id);

            Field fieldsField = ChangeTracker.class.getDeclaredField("fields");
            Field entityRelationKeysField = ChangeTracker.class.getDeclaredField("childEntityRelationKeys");
            Object changeTracker = loadedEntity.getContext().getChangeTracker();
            Collection fields = (Collection) ReflectionUtils.getFieldValue(fieldsField,changeTracker);
            fields.clear();
            Collection childEntityKeys = (Collection) ReflectionUtils.getFieldValue(entityRelationKeysField,changeTracker);
            childEntityKeys.clear();

            loadedEntity.setName("Changed-Name");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity reloadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, reloadedEntity, id);
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.MANUAL);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, loadedEntity,id);
            loadedEntity.setName("Changed-Name");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity reloadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, reloadedEntity, id);
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.setOne2OneEntity(new DirtyCheckTestOne2OneEntity());
            entity.getOne2OneEntity().setName("Child-Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, loadedEntity,id);
            loadedEntity.setOne2OneEntity(null);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToOne = existsOne2OneChild(tx,id);
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.MANUAL);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.setOne2OneEntity(new DirtyCheckTestOne2OneEntity());
            entity.getOne2OneEntity().setName("Child-Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, loadedEntity,id);
            loadedEntity.setOne2OneEntity(null);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToOne = existsOne2OneChild(tx,id);
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.setOne2OneEntity(new DirtyCheckTestOne2OneEntity());
            entity.getOne2OneEntity().setName("Child-Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx,loadedEntity, id);
            loadedEntity.getOne2OneEntity().setName("Child-Upd-Name");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity reLoadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.MANUAL);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.setOne2OneEntity(new DirtyCheckTestOne2OneEntity());
            entity.getOne2OneEntity().setName("Child-Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx,loadedEntity, id);
            loadedEntity.getOne2OneEntity().setName("Child-Upd-Name");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity reLoadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            DirtyCheckTestOne2ManyEntity one2ManyEntity = new DirtyCheckTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            entity.getOne2ManyEntities().add(one2ManyEntity);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx,loadedEntity,id);
            loadedEntity.getOne2ManyEntities().clear();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToOne = existsOne2ManyChild(tx,id);
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.MANUAL);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            DirtyCheckTestOne2ManyEntity one2ManyEntity = new DirtyCheckTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            entity.getOne2ManyEntities().add(one2ManyEntity);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx,loadedEntity,id);
            loadedEntity.getOne2ManyEntities().clear();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            boolean hasOneToOne = existsOne2ManyChild(tx,id);
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            DirtyCheckTestOne2ManyEntity one2ManyEntity = new DirtyCheckTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            entity.getOne2ManyEntities().add(one2ManyEntity);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx,loadedEntity,id);
            DirtyCheckTestOne2ManyEntity loadedOne2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            loadedOne2ManyEntity.setName("Child-Upd-Name");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity reloadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, reloadedEntity, id);
            DirtyCheckTestOne2ManyEntity reloadedOne2ManyEntity = reloadedEntity.getOne2ManyEntities().iterator().next();
            tx.close();

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
            connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.MANUAL);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            DirtyCheckTestRootEntity entity = new DirtyCheckTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");

            DirtyCheckTestOne2ManyEntity one2ManyEntity = new DirtyCheckTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Org-Name");
            entity.getOne2ManyEntities().add(one2ManyEntity);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity loadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx,loadedEntity,id);
            DirtyCheckTestOne2ManyEntity loadedOne2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            loadedOne2ManyEntity.setName("Child-Upd-Name");
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            DirtyCheckTestRootEntity reloadedEntity = new DirtyCheckTestRootEntity();
            loadEntityWithId(tx, reloadedEntity, id);
            DirtyCheckTestOne2ManyEntity reloadedOne2ManyEntity = reloadedEntity.getOne2ManyEntities().iterator().next();
            tx.close();

            Assert.assertEquals(reloadedOne2ManyEntity.getName(),one2ManyEntity.getName());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadEntityWithId(ITransaction tx, DirtyCheckTestRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from dirty_check_test_root where id_col = ?");
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

    private boolean existsOne2OneChild(ITransaction tx,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from dirty_check_test_one2one where id_col = ?");
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

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from dirty_check_test_one2many where id_col = ?");
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
