package dbgate;

import dbgate.utility.DBMgtUtility;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.impl.DbGate;
import dbgate.support.persistant.featureintegration.order.ItemTransaction;
import dbgate.support.persistant.featureintegration.order.ItemTransactionCharge;
import dbgate.support.persistant.featureintegration.order.Transaction;
import dbgate.support.persistant.featureintegration.product.Product;
import dbgate.support.persistant.featureintegration.product.Service;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateFeatureIntegrationTest
{
    private static DBConnector connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DbGateFeatureIntegrationTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:testing-feature_integreation;create=true");
            DBMgtUtility.close(con);
            
            connector = new DBConnector("jdbc:derby:memory:testing-feature_integreation;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);
            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(true);
            DbGate.getSharedInstance().getConfig().setCheckVersion(true);
            
            con = connector.getConnection();

            Collection<Class> dbClassList = new ArrayList<>();
            dbClassList.add(ItemTransaction.class);
            dbClassList.add(ItemTransactionCharge.class);
            dbClassList.add(Transaction.class);
            dbClassList.add(Product.class);
            dbClassList.add(Service.class);
            DbGate.getSharedInstance().patchDataBase(con,dbClassList,true);

            con.commit();
            con.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DbGateFeatureIntegrationTest.class.getName()).severe("Exception during database startup.");
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
            Connection connection = connector.getConnection();
            loadWithId(connection,loadedTransaction,transId);
            DBMgtUtility.close(connection);

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
                    Assert.assertEquals(orgItemTransaction.getItem().getName(),loadedItemTransaction.getItem().getName());
                    Assert.assertEquals(orgItemTransaction.getItem().getItemId(),loadedItemTransaction.getItem().getItemId());
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

    private boolean loadWithId(Connection connection, Transaction transaction,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from order_transaction where transaction_id = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            transaction.retrieve(rs,connection);
            loaded = true;
        }
        rs.close();
        ps.close();

        return loaded;
    }

    public Product createDefaultProduct(int productId) throws PersistException
    {
        Product product = new Product();
        product.setItemId(productId);
        product.setName("Product");
        product.setUnitPrice(54D);
        Connection con = connector.getConnection();
        product.persist(con);
        DBMgtUtility.close(con);
        return product;
    }

    public Service createDefaultService(int serviceId) throws PersistException
    {
        Service service = new Service();
        service.setItemId(serviceId);
        service.setName("Service");
        service.setHourlyRate(65D);
        Connection con = connector.getConnection();
        service.persist(con);
        DBMgtUtility.close(con);
        return service;
    }

    public Transaction createDefaultTransaction(int transactionId,Product product,Service service) throws PersistException
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

        Connection con = connector.getConnection();
        transaction.persist(con);
        DBMgtUtility.close(con);
        return transaction;
    }

    @After
    public void afterEach()
    {
    }

    @AfterClass
    public static void after()
    {
        Logger.getLogger(DbGateFeatureIntegrationTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:testing-feature_integreation;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("testing-feature_integreation").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}