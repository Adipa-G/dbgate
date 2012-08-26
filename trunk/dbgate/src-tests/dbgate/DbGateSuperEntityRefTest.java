package dbgate;

import dbgate.support.persistant.superentityrefinheritance.*;
import dbgate.utility.DBMgtUtility;
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
public class DbGateSuperEntityRefTest
{
    private static DefaultTransactionFactory connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DbGateSuperEntityRefTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-super_entity_ref_test;create=true");

            String sql = "Create table super_entity_ref_test_root (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(100) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table super_entity_ref_test_one2many (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tindex_no Int NOT NULL,\n" +
                        "\tname Varchar(100) NOT NULL,\n" +
                        " Primary Key (id_col,index_no))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table super_entity_ref_test_one2many_a (\n" +
                    "\tid_col Int NOT NULL,\n" +
                    "\tindex_no Int NOT NULL,\n" +
                    "\tname_a Varchar(100) NOT NULL,\n" +
                    " Primary Key (id_col,index_no))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table super_entity_ref_test_one2many_b (\n" +
                    "\tid_col Int NOT NULL,\n" +
                    "\tindex_no Int NOT NULL,\n" +
                    "\tname_b Varchar(100) NOT NULL,\n" +
                    " Primary Key (id_col,index_no))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table super_entity_ref_test_one2one (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(100) NOT NULL,\n" +
                        " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table super_entity_ref_test_one2one_a (\n" +
                    "\tid_col Int NOT NULL,\n" +
                    "\tname_a Varchar(100) NOT NULL,\n" +
                    " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table super_entity_ref_test_one2one_b (\n" +
                    "\tid_col Int NOT NULL,\n" +
                    "\tname_b Varchar(100) NOT NULL,\n" +
                    " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DefaultTransactionFactory("jdbc:derby:memory:unit-testing-super_entity_ref_test;","org.apache.derby.jdbc.EmbeddedDriver",
                                                      DefaultTransactionFactory.DB_DERBY);

            connector.getDbGate().getConfig().setAutoTrackChanges(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DbGateSuperEntityRefTest.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
    }

    @Test
    public void superEntityRef_persistAndLoadWithSingleTypeA_retrievedShouldBeSameAsPersisted()
    {
        try
        {
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 35;
            SuperEntityRefRootEntity entity = createDefaultRootEntity(id,1,0,true,false);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            SuperEntityRefRootEntity entityReloaded = new SuperEntityRefRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);
            tx.commit();
            tx.close();

            verifyEquals(entity,entityReloaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void superEntityRef_persistAndLoadWithSingleTypeB_retrievedShouldBeSameAsPersisted()
    {
        try
        {
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 35;
            SuperEntityRefRootEntity entity = createDefaultRootEntity(id,0,1,false,true);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            SuperEntityRefRootEntity entityReloaded = new SuperEntityRefRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);
            tx.commit();
            tx.close();

            verifyEquals(entity,entityReloaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void superEntityRef_persistAndLoadWithAllTypeA_retrievedShouldBeSameAsPersisted()
    {
        try
        {
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 35;
            SuperEntityRefRootEntity entity = createDefaultRootEntity(id,10,0,true,false);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            SuperEntityRefRootEntity entityReloaded = new SuperEntityRefRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);
            tx.commit();
            tx.close();

            verifyEquals(entity,entityReloaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void superEntityRef_persistAndLoadWithAllTypeB_retrievedShouldBeSameAsPersisted()
    {
        try
        {
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 35;
            SuperEntityRefRootEntity entity = createDefaultRootEntity(id,0,10,false,true);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            SuperEntityRefRootEntity entityReloaded = new SuperEntityRefRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);
            tx.commit();
            tx.close();

            verifyEquals(entity,entityReloaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void superEntityRef_persistAndLoadWithMixedTypes_retrievedShouldBeSameAsPersisted()
    {
        try
        {
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 35;
            SuperEntityRefRootEntity entity = createDefaultRootEntity(id,10,10,true,false);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            SuperEntityRefRootEntity entityReloaded = new SuperEntityRefRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);
            tx.commit();
            tx.close();

            verifyEquals(entity,entityReloaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void superEntityRef_persistAndLoadWithNullOneToOne_retrievedShouldBeSameAsPersisted()
    {
        try
        {
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 35;
            SuperEntityRefRootEntity entity = createDefaultRootEntity(id,0,0,false,false);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            SuperEntityRefRootEntity entityReloaded = new SuperEntityRefRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);
            tx.commit();
            tx.close();

            verifyEquals(entity,entityReloaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private void verifyEquals(SuperEntityRefRootEntity rootEntity, SuperEntityRefRootEntity loadedRootEntity)
    {
        Assert.assertEquals(loadedRootEntity.getIdCol(),rootEntity.getIdCol());
        Assert.assertEquals(loadedRootEntity.getName(),rootEntity.getName());

        for (SuperEntityRefOne2ManyEntity one2ManyEntity : rootEntity.getOne2ManyEntities())
        {
            boolean foundItem = false;
            for (SuperEntityRefOne2ManyEntity loadedOne2ManyEntity : loadedRootEntity.getOne2ManyEntities())
            {
                if (one2ManyEntity.getIndexNo() == loadedOne2ManyEntity.getIndexNo())
                {
                    foundItem = true;
                    Assert.assertEquals(one2ManyEntity.getClass(),loadedOne2ManyEntity.getClass());
                    Assert.assertEquals(one2ManyEntity.getName(), loadedOne2ManyEntity.getName());

                    if (one2ManyEntity instanceof SuperEntityRefOne2ManyEntityA)
                    {
                        SuperEntityRefOne2ManyEntityA one2ManyEntityA = (SuperEntityRefOne2ManyEntityA) one2ManyEntity;
                        SuperEntityRefOne2ManyEntityA loadedOne2ManyEntityA = (SuperEntityRefOne2ManyEntityA) loadedOne2ManyEntity;
                        Assert.assertEquals(one2ManyEntityA.getNameA(),loadedOne2ManyEntityA.getNameA());
                    }
                    else if (one2ManyEntity instanceof SuperEntityRefOne2ManyEntityB)
                    {
                        SuperEntityRefOne2ManyEntityB one2ManyEntityA = (SuperEntityRefOne2ManyEntityB) one2ManyEntity;
                        SuperEntityRefOne2ManyEntityB loadedOne2ManyEntityA = (SuperEntityRefOne2ManyEntityB) loadedOne2ManyEntity;
                        Assert.assertEquals(one2ManyEntityA.getNameB(),loadedOne2ManyEntityA.getNameB());
                    }
                }
            }
            Assert.assertTrue("Item rootEntity not found", foundItem);
        }

        SuperEntityRefOne2OneEntity one2OneEntity = rootEntity.getOne2OneEntity();
        SuperEntityRefOne2OneEntity loadedOne2OneEntity = loadedRootEntity.getOne2OneEntity();
        if (one2OneEntity == null || loadedOne2OneEntity == null)
        {
            Assert.assertTrue("One entity is null while other is not", one2OneEntity == loadedOne2OneEntity);
        }
        else
        {
            Assert.assertEquals(one2OneEntity.getClass(),loadedOne2OneEntity.getClass());
            Assert.assertEquals(one2OneEntity.getName(),loadedOne2OneEntity.getName());
            if (one2OneEntity instanceof SuperEntityRefOne2OneEntityA)
            {
                SuperEntityRefOne2OneEntityA one2OneEntityA = (SuperEntityRefOne2OneEntityA) one2OneEntity;
                SuperEntityRefOne2OneEntityA loadedOne2OneEntityA = (SuperEntityRefOne2OneEntityA) loadedOne2OneEntity;
                Assert.assertEquals(one2OneEntityA.getNameA(),loadedOne2OneEntityA.getNameA());
            }
            else if (one2OneEntity instanceof SuperEntityRefOne2OneEntityB)
            {
                SuperEntityRefOne2OneEntityB one2OneEntityB = (SuperEntityRefOne2OneEntityB) one2OneEntity;
                SuperEntityRefOne2OneEntityB loadedOne2OneEntityB = (SuperEntityRefOne2OneEntityB) loadedOne2OneEntity;
                Assert.assertEquals(one2OneEntityB.getNameB(),loadedOne2OneEntityB.getNameB());
            }
        }
    }

    private SuperEntityRefRootEntity createDefaultRootEntity(int id,int typeACount,int typeBCount,boolean one2OneIsA,boolean one2OneIsB) throws DbGateException
    {
        String entityText = String.format("Id->%s|A->%s|B->%s|OOA->%s|OOB->%s",id,typeACount,typeBCount,one2OneIsA,one2OneIsB);
        
        SuperEntityRefRootEntity rootEntity = new SuperEntityRefRootEntity();
        rootEntity.setIdCol(id);
        rootEntity.setName("Root-(" + entityText + ")");

        for (int i = 0; i < typeACount; i++)
        {
            rootEntity.getOne2ManyEntities().add(createOne2Many(true,entityText,i));
        }
        for (int i = typeACount; i < typeACount + typeBCount; i++)
        {
            rootEntity.getOne2ManyEntities().add(createOne2Many(false,entityText,i));
        }
        if (one2OneIsA)
        {
            rootEntity.setOne2OneEntity(createOne2One(true,entityText));
        }
        if (one2OneIsB)
        {
            rootEntity.setOne2OneEntity(createOne2One(false,entityText));
        }

        ITransaction tx = connector.createTransaction();
        rootEntity.persist(tx);
        DBMgtUtility.close(tx);
        return rootEntity;
    }

    private SuperEntityRefOne2ManyEntity createOne2Many(boolean typeA,String entityText,int index)
    {
        SuperEntityRefOne2ManyEntity entity = typeA
                ? new SuperEntityRefOne2ManyEntityA()
                : new SuperEntityRefOne2ManyEntityB();

        entity.setIndexNo(index);
        entity.setName("OM-S-(" +  entityText +  ")" + index);
        if (entity instanceof SuperEntityRefOne2ManyEntityA)
        {
            ((SuperEntityRefOne2ManyEntityA)entity).setNameA("OM-A-(" +  entityText +  ")" + index);
        }
        else if (entity instanceof SuperEntityRefOne2ManyEntityB)
        {
            ((SuperEntityRefOne2ManyEntityB)entity).setNameB("OM-B-(" + entityText + ")" + index);
        }
        return entity;
    }

    private SuperEntityRefOne2OneEntity createOne2One(boolean typeA,String entityText)
    {
        SuperEntityRefOne2OneEntity entity = typeA
                ? new SuperEntityRefOne2OneEntityA()
                : new SuperEntityRefOne2OneEntityB();

        entity.setName("OO-S-(" +  entityText +  ")");
        if (entity instanceof SuperEntityRefOne2OneEntityA)
        {
            ((SuperEntityRefOne2OneEntityA)entity).setNameA("OO-A-(" +  entityText +  ")");
        }
        else if (entity instanceof SuperEntityRefOne2OneEntityB)
        {
            ((SuperEntityRefOne2OneEntityB)entity).setNameB("OO-B-(" +  entityText +  ")");
        }
        return entity;
    }

    private boolean loadWithColumnEntityWithId(ITransaction tx, SuperEntityRefRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from super_entity_ref_test_root where id_col = ?");
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
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-super_entity_ref_test;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM super_entity_ref_test_root");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM super_entity_ref_test_one2many");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM super_entity_ref_test_one2many_a");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM super_entity_ref_test_one2many_b");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM super_entity_ref_test_one2one");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM super_entity_ref_test_one2one_a");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM super_entity_ref_test_one2one_b");
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
        Logger.getLogger(DbGateSuperEntityRefTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-super_entity_ref_test;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-super_entity_ref_test").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}
