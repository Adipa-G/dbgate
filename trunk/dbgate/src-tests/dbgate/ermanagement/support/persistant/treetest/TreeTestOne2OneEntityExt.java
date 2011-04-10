package dbgate.ermanagement.support.persistant.treetest;

import dbgate.DBClassStatus;
import dbgate.ermanagement.context.IEntityContext;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.ERLayer;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
public class TreeTestOne2OneEntityExt implements ITreeTestOne2OneEntity
{
    private DBClassStatus status;
    private int idCol;
    private String name;

    public TreeTestOne2OneEntityExt()
    {
        status = DBClassStatus.NEW;
    }

    public int getIdCol()
    {
        return idCol;
    }

    public void setIdCol(int idCol)
    {
        this.idCol = idCol;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public DBClassStatus getStatus()
    {
        return status;
    }

    public void setStatus(DBClassStatus status)
    {
        this.status = status;
    }

    public void persist(Connection con) throws PersistException
    {
        ERLayer.getSharedInstance().save(this,con);
    }

    public void retrieve(ResultSet rs, Connection con) throws RetrievalException
    {
        ERLayer.getSharedInstance().load(this,rs,con);
    }

    public IEntityContext getContext()
    {
        return null;
    }
}