package dbgate.one2manyexample;

import dbgate.ExampleBase;
import dbgate.ServerDBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.exceptions.DBPatchingException;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.one2manyexample.entities.One2ManyChildEntity;
import dbgate.one2manyexample.entities.One2ManyChildEntityA;
import dbgate.one2manyexample.entities.One2ManyChildEntityB;
import dbgate.one2manyexample.entities.One2ManyParentEntity;

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
public class One2ManyExample extends ExampleBase
{
    private static int id = 43;

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
    {
        Connection con = connector.getConnection();
        Collection<ServerDBClass> entities = new ArrayList<ServerDBClass>();
        entities.add(createEntityWithChildern());
        entities.add(new One2ManyChildEntityA());
        entities.add(new One2ManyChildEntityB());
        ERLayer.getSharedInstance().patchDataBase(con,entities,false);
        con.commit();
        DBMgmtUtility.close(con);
    }

    public void persist() throws PersistException, SQLException
    {
        Connection con = connector.getConnection();
        One2ManyParentEntity entity = createEntityWithChildern();
        entity.persist(con);
        con.commit();
        DBMgmtUtility.close(con);
    }

    public One2ManyParentEntity retrieve(int id) throws SQLException,RetrievalException
    {
        Connection con = connector.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from parent_entity where id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        One2ManyParentEntity entity = null;
        if (rs.next())
        {
            entity = new One2ManyParentEntity();
            entity.retrieve(rs,con);
        }
        DBMgmtUtility.close(rs);
        DBMgmtUtility.close(ps);
        DBMgmtUtility.close(con);
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
