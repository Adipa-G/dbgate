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
public class TreeTestOne2ManyEntityExt implements ITreeTestOne2ManyEntity
{
    private DBClassStatus status;
    private int idCol;
    private int indexNo;
    private String name;

    public TreeTestOne2ManyEntityExt()
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

    public int getIndexNo()
    {
        return indexNo;
    }

    public void setIndexNo(int indexNo)
    {
        this.indexNo = indexNo;
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