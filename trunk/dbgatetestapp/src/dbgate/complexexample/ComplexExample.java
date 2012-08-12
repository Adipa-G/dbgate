package dbgate.complexexample;

import dbgate.ExampleBase;
import dbgate.ServerDBClass;
import dbgate.complexexample.entities.order.ItemTransaction;
import dbgate.complexexample.entities.order.ItemTransactionCharge;
import dbgate.complexexample.entities.order.Transaction;
import dbgate.complexexample.entities.product.Product;
import dbgate.complexexample.entities.product.Service;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.exceptions.DBPatchingException;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.ERLayer;

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
        DBMgmtUtility.close(con);
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
        DBMgmtUtility.close(con);
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
        DBMgmtUtility.close(con);
        return transaction;
    }

    public void patch() throws DBPatchingException, SQLException
    {
        Connection con = connector.getConnection();
        Collection<ServerDBClass> entities = new ArrayList<ServerDBClass>();
        entities.add(new Transaction());
        entities.add(new ItemTransaction(new Transaction()));
        entities.add(new ItemTransactionCharge(new ItemTransaction(new Transaction())));
        entities.add(new Product());
        entities.add(new Service());
        ERLayer.getSharedInstance().patchDataBase(con,entities,false);
        con.commit();
        DBMgmtUtility.close(con);
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
        DBMgmtUtility.close(rs);
        DBMgmtUtility.close(ps);
        DBMgmtUtility.close(con);
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