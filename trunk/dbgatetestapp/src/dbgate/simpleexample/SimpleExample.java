package dbgate.simpleexample;

import dbgate.EntityStatus;
import dbgate.ExampleBase;
import dbgate.ermanagement.ermapper.DbGate;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.simpleexample.entities.SimpleEntity;
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
public class SimpleExample extends ExampleBase
{
    private int id = 43;

    public SimpleEntity createEntity()
    {
        SimpleEntity entity = new SimpleEntity();
        entity.setId(id);
        entity.setName("Entity");
        return entity;
    }

    public void patch() throws DBPatchingException, SQLException
    {
        Connection con = connector.getConnection();
        Collection<Class> entityTypes = new ArrayList<Class>();
        entityTypes.add(SimpleEntity.class);
        DbGate.getSharedInstance().patchDataBase(con,entityTypes,false);
        con.commit();
        DBMgtUtility.close(con);
    }

    public void persist(SimpleEntity entity) throws PersistException, SQLException
    {
        Connection con = connector.getConnection();
        entity.persist(con);
        con.commit();
        DBMgtUtility.close(con);
    }

    public SimpleEntity retrieve() throws SQLException,RetrievalException
    {
        Connection con = connector.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from simple_entity where id = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        SimpleEntity entity = null;
        if (rs.next())
        {
            entity = new SimpleEntity();
            entity.retrieve(rs,con);
        }
        DBMgtUtility.close(rs);
        DBMgtUtility.close(ps);
        DBMgtUtility.close(con);
        return entity;
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
            SimpleEntity entity = simpleExample.retrieve();
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
