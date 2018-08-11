package dbgate.simpleexample;

import dbgate.*;
import dbgate.ermanagement.query.SelectionQuery;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.exceptions.common.TransactionCommitFailedException;
import dbgate.exceptions.common.TransactionCreationFailedException;
import dbgate.simpleexample.entities.SimpleEntity;
import dbgate.utility.DBMgtUtility;
import docgenerate.WikiCodeBlock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 30, 2011
 * Time: 12:06:04 AM
 */
@WikiCodeBlock(id = "simple_example")
public class SimpleExample extends ExampleBase
{
    private int id = 43;

    public SimpleExample()
    {
        dbName = "simple_example";
    }

    public SimpleEntity createEntity()
    {
        SimpleEntity entity = new SimpleEntity();
        entity.setId(id);
        entity.setName("Entity");
        return entity;
    }

    public void patch() throws DBPatchingException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        Collection<Class> entityTypes = new ArrayList<Class>();
        entityTypes.add(SimpleEntity.class);
        factory.getDbGate().patchDataBase(tx,entityTypes,false);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public void persist(SimpleEntity entity) throws PersistException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        entity.persist(tx);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public SimpleEntity retrieve() throws SQLException,RetrievalException
            ,TransactionCreationFailedException
    {
        ITransaction tx = factory.createTransaction();
        PreparedStatement ps = tx.getConnection().prepareStatement("select * from simple_entity where id = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        SimpleEntity entity = null;
        if (rs.next())
        {
            entity = new SimpleEntity();
            entity.retrieve(rs,tx);
        }
        DBMgtUtility.close(rs);
        DBMgtUtility.close(ps);
        DBMgtUtility.close(tx);
        return entity;
    }

    public SimpleEntity retrieveWithQuery() throws SQLException,RetrievalException
            ,TransactionCreationFailedException
    {
        ITransaction tx = factory.createTransaction();
        ISelectionQuery query = new SelectionQuery()
                .from(QueryFrom.entityType(SimpleEntity.class))
                .select(QuerySelection.entityType(SimpleEntity.class));

        Collection entities = query.toList(tx);
        DBMgtUtility.close(tx);

        return (SimpleEntity)entities.iterator().next();
    }

    public static void main(String[] args)
    {
        SimpleExample simpleExample = new SimpleExample();
        simpleExample.initializeConnector();
        try
        {
            simpleExample.patch();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            SimpleEntity entity = simpleExample.createEntity();
            simpleExample.persist(entity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            SimpleEntity entity = simpleExample.retrieveWithQuery();
            System.out.println("entity.Name = " + entity.getName());

            entity.setName("Updated");
            simpleExample.persist(entity);
            entity = simpleExample.retrieve();
            System.out.println("entity.Name = " + entity.getName());

            entity.setStatus(EntityStatus.DELETED);
            simpleExample.persist(entity);
            entity = simpleExample.retrieve();
            System.out.println("entity = " + entity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        simpleExample.destroyConnector();
    }
}
