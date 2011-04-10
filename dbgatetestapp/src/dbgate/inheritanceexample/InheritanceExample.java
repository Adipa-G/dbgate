package dbgate.inheritanceexample;

import dbgate.ExampleBase;
import dbgate.ServerDBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.exceptions.DBPatchingException;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.inheritanceexample.entities.SubEntity;
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
public class InheritanceExample extends ExampleBase
{
    private int id = 43;

    public SubEntity createEntity()
    {
        SubEntity entity = new SubEntity();
        entity.setId(id);
        entity.setSuperName("Super");
        entity.setMiddleName("Middle");
        entity.setSubName("Sub");
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

    public void persist() throws PersistException, SQLException
    {
        Connection con = connector.getConnection();
        SubEntity entity = createEntity();
        entity.persist(con);
        con.commit();
        DBMgmtUtility.close(con);
    }

    public SubEntity retrieve() throws SQLException,RetrievalException
    {
        Connection con = connector.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from sub_entity where id = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        SubEntity entity = null;
        if (rs.next())
        {
            entity = new SubEntity();
            entity.retrieve(rs,con);
        }
        DBMgmtUtility.close(rs);
        DBMgmtUtility.close(ps);
        DBMgmtUtility.close(con);
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
            SubEntity entity = inheritanceExample.retrieve();
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
