package dbgate.persist;

import dbgate.AbstractDbGateTestBase;
import dbgate.DbGateException;
import dbgate.ITransaction;
import dbgate.persist.support.featureintegration.order.ItemTransaction;
import dbgate.persist.support.featureintegration.order.ItemTransactionCharge;
import dbgate.persist.support.featureintegration.order.Transaction;
import dbgate.persist.support.featureintegration.product.Item;
import dbgate.persist.support.featureintegration.product.Product;
import dbgate.persist.support.featureintegration.product.Service;
import dbgate.utility.DBMgtUtility;
import org.junit.*;

import java.sql.*;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateFeatureIntegrationTest extends AbstractDbGateTestBase
{
    private static final String dbName = "testing-feature_integreation";

    @BeforeClass
    public static void before()
    {
        testClass = DbGateFeatureIntegrationTest.class;
        beginInit(dbName);

        registerClassForDbPatching(ItemTransaction.class,dbName);
        registerClassForDbPatching(ItemTransactionCharge.class,dbName);
        registerClassForDbPatching(Transaction.class,dbName);
        registerClassForDbPatching(Product.class,dbName);
        registerClassForDbPatching(Service.class,dbName);
        registerClassForDbPatching(Service.class,dbName);

        endInit(dbName);
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
    }

    @Test
    public void featureIntegration_persistAndRetrieve_WithComplexStructure_retrievedShouldBeSameAsPersisted()
    {
        try
        {
            int transId = 35;
            int productId = 135;
            int serviceId = 235;

            Product product = createDefaultProduct(productId);
            Service service = createDefaultService(serviceId);
            Transaction transaction = createDefaultTransaction(transId,product,service);

            Transaction loadedTransaction = new Transaction();
            ITransaction tx = connector.createTransaction();
            loadWithId(tx,loadedTransaction,transId);
            tx.close();

            verifyEquals(transaction, loadedTransaction);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private void verifyEquals(Transaction transaction, Transaction loadedTransaction)
    {
        Assert.assertEquals(loadedTransaction.getName(),transaction.getName());
        for (ItemTransaction orgItemTransaction : transaction.getItemTransactions())
        {
            boolean foundItem = false;
            for (ItemTransaction loadedItemTransaction : loadedTransaction.getItemTransactions())
            {
                if (orgItemTransaction.getIndexNo() == loadedItemTransaction.getIndexNo())
                {
                    foundItem = true;
                    Item orgItem = orgItemTransaction.getItem();
                    Item loadedItem = loadedItemTransaction.getItem();

                    Assert.assertEquals(orgItem.getClass(),loadedItem.getClass());
                    Assert.assertEquals(orgItem.getName(),loadedItem.getName());
                    Assert.assertEquals(orgItem.getItemId(),loadedItem.getItemId());
                    Assert.assertSame(loadedItemTransaction.getTransaction(),loadedTransaction);

                    for (ItemTransactionCharge orgTransactionCharge : orgItemTransaction.getItemTransactionCharges())
                    {
                        boolean foundCharge = false;
                        for (ItemTransactionCharge loadedTransactionCharge : loadedItemTransaction.getItemTransactionCharges())
                        {
                            if ( orgTransactionCharge.getIndexNo() == loadedTransactionCharge.getIndexNo()
                                    && orgTransactionCharge.getChargeIndex() == loadedTransactionCharge.getChargeIndex())
                            {
                                foundCharge = true;
                                Assert.assertEquals(orgTransactionCharge.getChargeCode(),loadedTransactionCharge.getChargeCode());
                                Assert.assertSame(loadedTransactionCharge.getTransaction(),loadedTransaction);
                                Assert.assertSame(loadedTransactionCharge.getItemTransaction(),loadedItemTransaction);
                            }
                        }
                        Assert.assertTrue("Item transaction charge not found", foundCharge);
                    }
                }
            }
            Assert.assertTrue("Item transaction not found", foundItem);
        }
        Assert.assertEquals(loadedTransaction.getName(),transaction.getName());
    }

    private boolean loadWithId(ITransaction tx, Transaction transaction,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from order_transaction where transaction_id = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            transaction.retrieve(rs,tx);
            loaded = true;
        }
        rs.close();
        ps.close();

        return loaded;
    }

    public Product createDefaultProduct(int productId) throws DbGateException
    {
        Product product = new Product();
        product.setItemId(productId);
        product.setName("Product");
        product.setUnitPrice(54D);
        ITransaction tx = connector.createTransaction();
        product.persist(tx);
        DBMgtUtility.close(tx);
        return product;
    }

    public Service createDefaultService(int serviceId) throws DbGateException
    {
        Service service = new Service();
        service.setItemId(serviceId);
        service.setName("Service");
        service.setHourlyRate(65D);
        ITransaction tx = connector.createTransaction();
        service.persist(tx);
        DBMgtUtility.close(tx);
        return service;
    }

    public Transaction createDefaultTransaction(int transactionId,Product product,Service service) throws DbGateException
    {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setName("TRS-0001");

        ItemTransaction productTransaction = new ItemTransaction(transaction);
        productTransaction.setIndexNo(0);
        productTransaction.setItem(product);
        transaction.getItemTransactions().add(productTransaction);

        ItemTransactionCharge productTransactionCharge = new ItemTransactionCharge(productTransaction);
        productTransactionCharge.setChargeCode("Product-Sell-Code");
        productTransaction.getItemTransactionCharges().add(productTransactionCharge);

        ItemTransaction serviceTransaction = new ItemTransaction(transaction);
        serviceTransaction.setIndexNo(1);
        serviceTransaction.setItem(service);
        transaction.getItemTransactions().add(serviceTransaction);

        ItemTransactionCharge serviceTransactionCharge = new ItemTransactionCharge(serviceTransaction);
        serviceTransactionCharge.setChargeCode("Service-Sell-Code");
        serviceTransaction.getItemTransactionCharges().add(serviceTransactionCharge);

        ITransaction con = connector.createTransaction();
        transaction.persist(con);
        DBMgtUtility.close(con);
        return transaction;
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