package dbgate.complexexample;

import dbgate.ExampleBase;
import dbgate.ITransaction;
import dbgate.complexexample.entities.order.ItemTransaction;
import dbgate.complexexample.entities.order.ItemTransactionCharge;
import dbgate.complexexample.entities.order.Transaction;
import dbgate.complexexample.entities.product.Product;
import dbgate.complexexample.entities.product.Service;
import dbgate.ermanagement.ermapper.DbGate;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.exceptions.common.TransactionCommitFailedException;
import dbgate.exceptions.common.TransactionCreationFailedException;
import dbgate.utility.DBMgtUtility;
import docgenerate.WikiCodeBlock;

import java.lang.annotation.Documented;
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
@WikiCodeBlock(id = "complex_example")
public class ComplexExample extends ExampleBase
{
    private static int productId = 321;
    private static int serviceId = 322;
    private static int transactionId = 43;

    public ComplexExample()
    {
        dbName = "complex_example";
    }

    public Product createDefaultProduct() throws PersistException
            ,TransactionCreationFailedException
    {
        Product product = new Product();
        product.setItemId(productId);
        product.setName("Product");
        product.setUnitPrice(54D);
        ITransaction tx = factory.createTransaction();
        product.persist(tx);
        DBMgtUtility.close(tx);
        return product;
    }

    public Service createDefaultService() throws PersistException
            ,TransactionCreationFailedException
    {
        Service service = new Service();
        service.setItemId(serviceId);
        service.setName("Service");
        service.setHourlyRate(65D);
        ITransaction tx = factory.createTransaction();
        service.persist(tx);
        DBMgtUtility.close(tx);
        return service;
    }

    public Transaction createDefaultTransaction() throws PersistException
            ,TransactionCreationFailedException
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

        ITransaction tx = factory.createTransaction();
        transaction.persist(tx);
        DBMgtUtility.close(tx);
        return transaction;
    }

    public void patch() throws DBPatchingException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        Collection<Class> entityTypes = new ArrayList<Class>();
        entityTypes.add(Transaction.class);
        entityTypes.add(ItemTransaction.class);
        entityTypes.add(ItemTransactionCharge.class);
        entityTypes.add(Product.class);
        entityTypes.add(Service.class);
        factory.getDbGate().patchDataBase(tx, entityTypes, false);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public Transaction retrieve(int id) throws SQLException,RetrievalException
            ,TransactionCreationFailedException
    {
        ITransaction tx = factory.createTransaction();
        PreparedStatement ps = tx.getConnection().prepareStatement(
                "select * from order_transaction where transaction_Id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Transaction entity = null;
        if (rs.next())
        {
            entity = new Transaction();
            entity.retrieve(rs,tx);
        }
        DBMgtUtility.close(rs);
        DBMgtUtility.close(ps);
        DBMgtUtility.close(tx);
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
