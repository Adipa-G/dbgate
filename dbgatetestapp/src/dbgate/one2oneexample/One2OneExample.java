package dbgate.one2oneexample;

import dbgate.ExampleBase;
import dbgate.ServerDBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.exceptions.DBPatchingException;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.one2oneexample.entities.One2OneChildEntityA;
import dbgate.one2oneexample.entities.One2OneChildEntityB;
import dbgate.one2oneexample.entities.One2OneParentEntity;

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
public class One2OneExample extends ExampleBase
{
    private static int idA = 43;
    private static int idB = 44;

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
    {
        Connection con = connector.getConnection();
        Collection<ServerDBClass> entities = new ArrayList<ServerDBClass>();
        entities.add(createEntityWithChildA());
        entities.add(new One2OneChildEntityA());
        entities.add(new One2OneChildEntityB());
        ERLayer.getSharedInstance().patchDataBase(con,entities,false);
        con.commit();
        DBMgmtUtility.close(con);
    }

    public void persistWithA() throws PersistException, SQLException
    {
        Connection con = connector.getConnection();
        One2OneParentEntity entity = createEntityWithChildA();
        entity.persist(con);
        con.commit();
        DBMgmtUtility.close(con);
    }

    public void persistWithB() throws PersistException, SQLException
    {
        Connection con = connector.getConnection();
        One2OneParentEntity entity = createEntityWithChildB();
        entity.persist(con);
        con.commit();
        DBMgmtUtility.close(con);
    }

    public One2OneParentEntity retrieve(int id) throws SQLException,RetrievalException
    {
        Connection con = connector.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from parent_entity where id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        One2OneParentEntity entity = null;
        if (rs.next())
        {
            entity = new One2OneParentEntity();
            entity.retrieve(rs,con);
        }
        DBMgmtUtility.close(rs);
        DBMgmtUtility.close(ps);
        DBMgmtUtility.close(con);
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
