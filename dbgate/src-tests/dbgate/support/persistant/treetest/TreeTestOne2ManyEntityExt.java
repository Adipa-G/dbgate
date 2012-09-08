package dbgate.support.persistant.treetest;

import dbgate.EntityStatus;
import dbgate.ITransaction;
import dbgate.context.IEntityContext;
import dbgate.context.impl.EntityContext;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;

import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
public class TreeTestOne2ManyEntityExt implements ITreeTestOne2ManyEntity
{
    private EntityContext context;
    private EntityStatus status;
    private int idCol;
    private int indexNo;
    private String name;

    public TreeTestOne2ManyEntityExt()
    {
        context = new EntityContext();
        status = EntityStatus.NEW;
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

    public EntityStatus getStatus()
    {
        return status;
    }

    public void setStatus(EntityStatus status)
    {
        this.status = status;
    }

    public void persist(ITransaction tx) throws PersistException
    {
        tx.getDbGate().save(this,tx);
    }

    public void retrieve(ResultSet rs, ITransaction tx) throws RetrievalException
    {
        tx.getDbGate().load(this,rs,tx);
    }

    public IEntityContext getContext()
    {
        return context;
    }
}