package dbgate.simpleexample;

import dbgate.DBClassStatus;
import dbgate.ExampleBase;
import dbgate.ServerDBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.exceptions.DBPatchingException;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.simpleexample.entities.SimpleEntity;

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
        Collection<ServerDBClass> entities = new ArrayList<ServerDBClass>();
        entities.add(createEntity());
        ERLayer.getSharedInstance().patchDataBase(con,entities,false);
        con.commit();
        DBMgmtUtility.close(con);
    }

    public void persist(SimpleEntity entity) throws PersistException, SQLException
    {
        Connection con = connector.getConnection();
        entity.persist(con);
        con.commit();
        DBMgmtUtility.close(con);
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
        DBMgmtUtility.close(rs);
        DBMgmtUtility.close(ps);
        DBMgmtUtility.close(con);
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

            entity.setStatus(DBClassStatus.DELETED);
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
