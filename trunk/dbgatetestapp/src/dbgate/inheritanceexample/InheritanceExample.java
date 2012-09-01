package dbgate.inheritanceexample;

import dbgate.ExampleBase;
import dbgate.ITransaction;
import dbgate.ermanagement.ermapper.DbGate;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.exceptions.common.TransactionCommitFailedException;
import dbgate.exceptions.common.TransactionCreationFailedException;
import dbgate.inheritanceexample.entities.BottomEntity;
import dbgate.inheritanceexample.entities.MiddleEntity;
import dbgate.inheritanceexample.entities.TopEntity;
import dbgate.utility.DBMgtUtility;
import docgenerate.WikiCodeBlock;

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
@WikiCodeBlock(id = "inheritance_example")
public class InheritanceExample extends ExampleBase
{
    private int id = 43;

    public InheritanceExample()
    {
        dbName = "inheritance_example";
    }

    public BottomEntity createEntity()
    {
        BottomEntity entity = new BottomEntity();
        entity.setId(id);
        entity.setSuperName("Super");
        entity.setMiddleName("Middle");
        entity.setSubName("Sub");
        return entity;
    }

    public void patch() throws DBPatchingException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        Collection<Class> entityTypes = new ArrayList<Class>();
        entityTypes.add(TopEntity.class);
        entityTypes.add(BottomEntity.class);
        entityTypes.add(MiddleEntity.class);
        factory.getDbGate().patchDataBase(tx, entityTypes, false);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public void persist() throws PersistException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        BottomEntity entity = createEntity();
        entity.persist(tx);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public BottomEntity retrieve() throws SQLException,RetrievalException
            ,TransactionCreationFailedException
    {
        ITransaction tx = factory.createTransaction();
        PreparedStatement ps = tx.getConnection().prepareStatement("select * from bottom_entity where id = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        BottomEntity entity = null;
        if (rs.next())
        {
            entity = new BottomEntity();
            entity.retrieve(rs,tx);
        }
        DBMgtUtility.close(rs);
        DBMgtUtility.close(ps);
        DBMgtUtility.close(tx);
        return entity;
    }

    public static void main(String[] args)
    {
        InheritanceExample inheritanceExample = new InheritanceExample();
        inheritanceExample.initializeConnector();
        try
        {
            inheritanceExample.patch();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            inheritanceExample.persist();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            BottomEntity entity = inheritanceExample.retrieve();
            System.out.println("entity.SuperName = " + entity.getSuperName());
            System.out.println("entity.MiddleName = " + entity.getMiddleName());
            System.out.println("entity.SubName = " + entity.getSubName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        inheritanceExample.destroyConnector();
    }
}
