package dbgate.complexexample;

import dbgate.ExampleBase;
import dbgate.complexexample.entities.order.ItemTransaction;
import dbgate.complexexample.entities.order.ItemTransactionCharge;
import dbgate.complexexample.entities.order.Transaction;
import dbgate.complexexample.entities.product.Product;
import dbgate.complexexample.entities.product.Service;
import dbgate.ermanagement.ermapper.DbGate;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.utility.DBMgtUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 30, 2011
 * Time: 12:06:04 AM
 */
public class ComplexExample extends ExampleBase
{
    private static int productId = 321;
    private static int serviceId = 322;
    private static int transactionId = 43;

    public Product createDefaultProduct() throws PersistException
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

    public Service createDefaultService() throws PersistException
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

    public Transaction createDefaultTransaction() throws PersistException
    {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setName("TRS-0001");

        ItemTransaction productTransaction = new ItemTransaction(transaction);
        productTransaction.setIndexNo(0);
        productTransaction.setItem(createDefaultProduct());
        transaction.getItemTransactions().add(productTransaction);

        ItemTransactionCharge productTransactionCharge = new ItemTransactionCharge(productTransaction);
        productTransactionCharge.setChargeCode("Product-Sell-Code");
        productTransaction.getItemTransactionCharges().add(productTransactionCharge);

        ItemTransaction serviceTransaction = new ItemTransaction(transaction);
        serviceTransaction.setIndexNo(0);
        serviceTransaction.setItem(createDefaultService());
        transaction.getItemTransactions().add(serviceTransaction);

        ItemTransactionCharge serviceTransactionCharge = new ItemTransactionCharge(serviceTransaction);
        serviceTransactionCharge.setChargeCode("Service-Sell-Code");
        serviceTransaction.getItemTransactionCharges().add(serviceTransactionCharge);

        Connection con = connector.getConnection();
        transaction.persist(con);
        DBMgtUtility.close(con);
        return transaction;
    }

    public void patch() throws DBPatchingException, SQLException
    {
        Connection con = connector.getConnection();
        Collection<Class> entityTypes = new ArrayList<Class>();
        entityTypes.add(Transaction.class);
        entityTypes.add(ItemTransaction.class);
        entityTypes.add(ItemTransactionCharge.class);
        entityTypes.add(Product.class);
        entityTypes.add(Service.class);
        DbGate.getSharedInstance().patchDataBase(con,entityTypes,false);
        con.commit();
        DBMgtUtility.close(con);
    }

    public Transaction retrieve(int id) throws SQLException,RetrievalException
    {
        Connection con = connector.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from order_transaction where transaction_Id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Transaction entity = null;
        if (rs.next())
        {
            entity = new Transaction();
            entity.retrieve(rs,con);
        }
        DBMgtUtility.close(rs);
        DBMgtUtility.close(ps);
        DBMgtUtility.close(con);
        return entity;
    }

    public static void main(String[] args)
    {
        ComplexExample complexExample = new ComplexExample();
        complexExample.initializeConnector();
        try
        {
            complexExample.patch();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            complexExample.createDefaultTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            Transaction entity = complexExample.retrieve(transactionId);
            System.out.println("entityA.Name() = " + entity.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        complexExample.destroyConnector();
    }
}
