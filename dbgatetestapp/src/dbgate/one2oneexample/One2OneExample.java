package dbgate.one2oneexample;

import dbgate.ExampleBase;
import dbgate.ITransaction;
import dbgate.ermanagement.ermapper.DbGate;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.exceptions.common.TransactionCommitFailedException;
import dbgate.exceptions.common.TransactionCreationFailedException;
import dbgate.one2oneexample.entities.One2OneChildEntityA;
import dbgate.one2oneexample.entities.One2OneChildEntityB;
import dbgate.one2oneexample.entities.One2OneParentEntity;
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
@WikiCodeBlock(id = "one_2_one_example")
public class One2OneExample extends ExampleBase
{
    private static int idA = 43;
    private static int idB = 44;

    public One2OneExample()
    {
        dbName = "one_2_one_example";
    }

    public One2OneParentEntity createEntityWithChildA()
    {
        One2OneParentEntity entity = new One2OneParentEntity();
        entity.setId(idA);
        entity.setName("Parent");

        entity.setChildEntity(new One2OneChildEntityA());
        entity.getChildEntity().setName("Child A");

        return entity;
    }

    public One2OneParentEntity createEntityWithChildB()
    {
        One2OneParentEntity entity = new One2OneParentEntity();
        entity.setId(idB);
        entity.setName("Parent");

        entity.setChildEntity(new One2OneChildEntityB());
        entity.getChildEntity().setName("Child B");

        return entity;
    }

    public void patch() throws DBPatchingException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        Collection<Class> entityTypes = new ArrayList<Class>();
        entityTypes.add(One2OneParentEntity.class);
        entityTypes.add(One2OneChildEntityA.class);
        entityTypes.add(One2OneChildEntityB.class);
        factory.getDbGate().patchDataBase(tx, entityTypes, false);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public void persistWithA() throws PersistException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        One2OneParentEntity entity = createEntityWithChildA();
        entity.persist(tx);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public void persistWithB() throws PersistException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        One2OneParentEntity entity = createEntityWithChildB();
        entity.persist(tx);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public One2OneParentEntity retrieve(int id) throws SQLException,RetrievalException
            ,TransactionCreationFailedException
    {
        ITransaction tx = factory.createTransaction();
        PreparedStatement ps = tx.getConnection().prepareStatement("select * from parent_entity where id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        One2OneParentEntity entity = null;
        if (rs.next())
        {
            entity = new One2OneParentEntity();
            entity.retrieve(rs,tx);
        }
        DBMgtUtility.close(rs);
        DBMgtUtility.close(ps);
        DBMgtUtility.close(tx);
        return entity;
    }

    public static void main(String[] args)
    {
        One2OneExample inheritanceExample = new One2OneExample();
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
            inheritanceExample.persistWithA();
            inheritanceExample.persistWithB();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            One2OneParentEntity entity = inheritanceExample.retrieve(idA);
            System.out.println("entityA.Name() = " + entity.getName());
            System.out.println("entityA.ChildName() = " + entity.getChildEntity().getName());

            entity = inheritanceExample.retrieve(idB);
            System.out.println("entityB.Name() = " + entity.getName());
            System.out.println("entityB.ChildName() = " + entity.getChildEntity().getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        inheritanceExample.destroyConnector();
    }
}
