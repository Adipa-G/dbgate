package dbgate;

import dbgate.support.persistant.nonidentifyingrelationwithoutcolumn.Currency;
import dbgate.support.persistant.nonidentifyingrelationwithoutcolumn.Product;
import junit.framework.Assert;
import org.junit.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Aug 29, 2010
 * Time: 6:40:58 PM
 */
public class DbGateNonIdentifyingRelationWithoutColumnTests extends AbstractDbGateTestBase
{
    private static final String dbName = "unit-testing-relations";

    @BeforeClass
    public static void before()
    {
        testClass = DbGateNonIdentifyingRelationWithoutColumnTests.class;
        beginInit(dbName);

        registerClassForDbPatching(Product.class,dbName);
        registerClassForDbPatching(Currency.class,dbName);

        endInit(dbName);

        connector.getDbGate().getConfig().setAutoTrackChanges(false);
        connector.getDbGate().getConfig().setCheckVersion(false);
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
        connector.getDbGate().getConfig().setAutoTrackChanges(true);
    }

    @Test
    public void nonIdentifyingRelationWithoutColumn_persist_withRelation_loadedShouldBeSameAsPersisted()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            int currencyId = 45;
            int productId = 55;
            
            Currency currency = new Currency();
            currency.setCurrencyId(currencyId);
            currency.setCode("LKR");
            currency.persist(tx);
            
            Product product = new Product();
            product.setProductId(productId);
            product.setPrice(300);
            product.setCurrency(currency);
            product.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            Product loaded = loadProductWithId(tx,productId);
            Assert.assertNotNull(loaded);
            Assert.assertNotNull(loaded.getCurrency());
            Assert.assertEquals(loaded.getCurrency().getCurrencyId(), currency.getCurrencyId());
            Assert.assertEquals(loaded.getCurrency().getCode(), currency.getCode());
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void nonIdentifyingRelationWithoutColumn_persist_withRelation_updateShouldBeSameAsPersisted()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            int currencyAId = 35;
            int currencyBId = 45;
            int productId = 55;

            Currency currencyA = new Currency();
            currencyA.setCurrencyId(currencyAId);
            currencyA.setCode("LKR");
            currencyA.persist(tx);

            Currency currencyB = new Currency();
            currencyB.setCurrencyId(currencyBId);
            currencyB.setCode("USD");
            currencyB.persist(tx);

            Product product = new Product();
            product.setProductId(productId);
            product.setPrice(300);
            product.setCurrency(currencyA);
            product.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            Product loaded = loadProductWithId(tx,productId);
            loaded.setCurrency(currencyB);
            loaded.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            loaded = loadProductWithId(tx,productId);
            Assert.assertNotNull(loaded);
            Assert.assertNotNull(loaded.getCurrency());
            Assert.assertEquals(loaded.getCurrency().getCurrencyId(),currencyB.getCurrencyId());
            Assert.assertEquals(loaded.getCurrency().getCode(),currencyB.getCode());
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void nonIdentifyingRelationWithoutColumn_persist_withRelation_deleteShouldBeSameAsPersisted()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            int currencyId = 45;
            int productId = 55;

            Currency currency = new Currency();
            currency.setCurrencyId(currencyId);
            currency.setCode("LKR");
            currency.persist(tx);

            Product product = new Product();
            product.setProductId(productId);
            product.setPrice(300);
            product.setCurrency(currency);
            product.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            Product loaded = loadProductWithId(tx,productId);
            loaded.setCurrency(null);
            loaded.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            loaded = loadProductWithId(tx,productId);
            Assert.assertNotNull(loaded);
            Assert.assertNull(loaded.getCurrency());
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private Product loadProductWithId(ITransaction tx,int id) throws Exception
    {
        Product loadedEntity = null;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from relation_test_product where product_id = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadedEntity = new Product();
            loadedEntity.retrieve(rs,tx);
        }
        rs.close();
        ps.close();

        return loadedEntity;
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