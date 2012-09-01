package dbgate.one2manyexample;

import dbgate.ExampleBase;
import dbgate.ITransaction;
import dbgate.ermanagement.ermapper.DbGate;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.exceptions.common.TransactionCommitFailedException;
import dbgate.exceptions.common.TransactionCreationFailedException;
import dbgate.one2manyexample.entities.One2ManyChildEntity;
import dbgate.one2manyexample.entities.One2ManyChildEntityA;
import dbgate.one2manyexample.entities.One2ManyChildEntityB;
import dbgate.one2manyexample.entities.One2ManyParentEntity;
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
@WikiCodeBlock(id = "one_2_many_example")
public class One2ManyExample extends ExampleBase
{
    private static int id = 43;

    public One2ManyExample()
    {
        dbName = "one_2_many_example";
    }

    public One2ManyParentEntity createEntityWithChildern()
    {
        One2ManyParentEntity entity = new One2ManyParentEntity();
        entity.setId(id);
        entity.setName("Parent");

        One2ManyChildEntityA childEntityA = new One2ManyChildEntityA();
        childEntityA.setName("Child A");
        entity.getChildEntities().add(childEntityA);

        One2ManyChildEntityB childEntityB = new One2ManyChildEntityB();
        childEntityB.setName("Child B");
        entity.getChildEntities().add(childEntityB);

        return entity;
    }

    public void patch() throws DBPatchingException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        Collection<Class> entityTypes = new ArrayList<Class>();
        entityTypes.add(One2ManyParentEntity.class);
        entityTypes.add(One2ManyChildEntityA.class);
        entityTypes.add(One2ManyChildEntityB.class);
        factory.getDbGate().patchDataBase(tx, entityTypes, false);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public void persist() throws PersistException, SQLException
            ,TransactionCreationFailedException,TransactionCommitFailedException
    {
        ITransaction tx = factory.createTransaction();
        One2ManyParentEntity entity = createEntityWithChildern();
        entity.persist(tx);
        tx.commit();
        DBMgtUtility.close(tx);
    }

    public One2ManyParentEntity retrieve(int id) throws SQLException,RetrievalException
            ,TransactionCreationFailedException
    {
        ITransaction tx = factory.createTransaction();
        PreparedStatement ps = tx.getConnection().prepareStatement("select * from parent_entity where id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        One2ManyParentEntity entity = null;
        if (rs.next())
        {
            entity = new One2ManyParentEntity();
            entity.retrieve(rs,tx);
        }
        DBMgtUtility.close(rs);
        DBMgtUtility.close(ps);
        DBMgtUtility.close(tx);
        return entity;
    }

    public static void main(String[] args)
    {
        One2ManyExample one2ManyExample = new One2ManyExample();
        one2ManyExample.initializeConnector();
        try
        {
            one2ManyExample.patch();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            one2ManyExample.persist();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            One2ManyParentEntity entity = one2ManyExample.retrieve(id);
            System.out.println("entity.Name() = " + entity.getName());
            for (One2ManyChildEntity childEntity : entity.getChildEntities())
            {
                System.out.println("entity.ChildName() = " + childEntity.getName());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        one2ManyExample.destroyConnector();
    }
}
