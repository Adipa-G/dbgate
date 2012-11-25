package dbgate.persist;

import dbgate.AbstractDbGateTestBase;
import dbgate.DirtyCheckStrategy;
import dbgate.ITransaction;
import dbgate.persist.support.lazy.LazyOne2ManyEntity;
import dbgate.persist.support.lazy.LazyOne2OneEntity;
import dbgate.persist.support.lazy.LazyRootEntity;
import net.sf.cglib.proxy.Enhancer;
import org.junit.*;

import java.sql.*;
import java.util.Iterator;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateLazyTest extends AbstractDbGateTestBase
{
    private static final String dbName = "unit-testing-fetchStrategy";

    @BeforeClass
    public static void before()
    {
        testClass = DbGateLazyTest.class;
        beginInit(dbName);

        String sql = "Create table lazy_test_root (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        sql = "Create table lazy_test_one2many (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tindex_no Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col,index_no))";
        createTableFromSql(sql,dbName);

        sql = "Create table lazy_test_one2one (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);
        endInit(dbName);
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().getStatistics().reset();
        connector.getDbGate().clearCache();

        connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);

    }

    @Test
    public void lazy_persistAndLoad_WithEmptyLazyFieldsWithLazyOn_shouldHaveProxiesForLazyFields()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            connector.getDbGate().getConfig().setEnableStatistics(true);
            connector.getDbGate().getStatistics().reset();

            int id = 35;
            LazyRootEntity entity = new LazyRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            LazyRootEntity entityReloaded = new LazyRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);
            tx.commit();
            tx.close();

            boolean isEnhancedOneToMany = Enhancer.isEnhanced(entityReloaded.getOne2ManyEntities().getClass());
            boolean isEnhancedOneToOne = Enhancer.isEnhanced(entityReloaded.getOne2ManyEntities().getClass());
            Assert.assertTrue(isEnhancedOneToMany);
            Assert.assertTrue(isEnhancedOneToOne);
            Assert.assertTrue(connector.getDbGate().getStatistics().getSelectQueryCount() == 0);
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
            ITransaction tx = connector.createTransaction();
            connector.getDbGate().getConfig().setEnableStatistics(true);
            connector.getDbGate().getStatistics().reset();

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

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            LazyRootEntity entityReloaded = new LazyRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);

            Assert.assertTrue(entityReloaded.getOne2ManyEntities().size() == 2);
            Iterator<LazyOne2ManyEntity> iterator = entityReloaded.getOne2ManyEntities().iterator();
            Assert.assertTrue(iterator.next().getName().equals(one2Many1.getName()));
            Assert.assertTrue(iterator.next().getName().equals(one2Many2.getName()));
            Assert.assertTrue(entityReloaded.getOne2OneEntity() != null);
            Assert.assertTrue(entityReloaded.getOne2OneEntity().getName().equals(one2One.getName()));
            Assert.assertTrue(connector.getDbGate().getStatistics().getSelectQueryCount() == 2);

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
    public void lazy_persistAndLoad_WithLazyOnWithValuesInLazyFields_shouldRetrieveLazyFieldsInAnotherConnection()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            connector.getDbGate().getConfig().setEnableStatistics(true);
            connector.getDbGate().getStatistics().reset();

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

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            LazyRootEntity entityReloaded = new LazyRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);
            tx.commit();
            tx.close();

            Assert.assertTrue(entityReloaded.getOne2ManyEntities().size() == 2);
            Iterator<LazyOne2ManyEntity> iterator = entityReloaded.getOne2ManyEntities().iterator();
            Assert.assertTrue(iterator.next().getName().equals(one2Many1.getName()));
            Assert.assertTrue(iterator.next().getName().equals(one2Many2.getName()));
            Assert.assertTrue(entityReloaded.getOne2OneEntity() != null);
            Assert.assertTrue(entityReloaded.getOne2OneEntity().getName().equals(one2One.getName()));
            Assert.assertTrue(connector.getDbGate().getStatistics().getSelectQueryCount() == 2);
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
            ITransaction tx = connector.createTransaction();
            connector.getDbGate().getConfig().setEnableStatistics(true);
            connector.getDbGate().getStatistics().reset();

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

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            LazyRootEntity entityReloaded = new LazyRootEntity();
            loadWithColumnEntityWithId(tx,entityReloaded,id);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            entityReloaded.persist(tx);
            tx.commit();
            tx.close();

            Assert.assertTrue(connector.getDbGate().getStatistics().getSelectQueryCount() == 0);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean loadWithColumnEntityWithId(ITransaction tx, LazyRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from lazy_test_root where id_col = ?");
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
        cleanupDb(dbName);
    }

    @AfterClass
    public static void after()
    {
        finalizeDb(dbName);
    }
}
