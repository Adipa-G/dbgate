package dbgate.support.persistant.treetest;

import dbgate.EntityStatus;
import dbgate.ermanagement.context.IEntityContext;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.DbGate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
public class TreeTestRootEntityExt implements ITreeTestRootEntity
{
    private EntityStatus status;
    private int idCol;
    private String name;
    private Collection<ITreeTestOne2ManyEntity> one2ManyEntities;
    private ITreeTestOne2OneEntity one2OneEntity;

    public TreeTestRootEntityExt()
    {
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Collection<ITreeTestOne2ManyEntity> getOne2ManyEntities()
    {
        return one2ManyEntities;
    }

    public void setOne2ManyEntities(Collection<ITreeTestOne2ManyEntity> one2ManyEntities)
    {
        this.one2ManyEntities = one2ManyEntities;
    }

    public ITreeTestOne2OneEntity getOne2OneEntity()
    {
        return one2OneEntity;
    }

    public void setOne2OneEntity(ITreeTestOne2OneEntity one2OneEntity)
    {
        this.one2OneEntity = one2OneEntity;
    }

    public EntityStatus getStatus()
    {
        return status;
    }

    public void setStatus(EntityStatus status)
    {
        this.status = status;
    }

    public void persist(Connection con) throws PersistException
    {
        DbGate.getSharedInstance().save(this,con);
    }

    public void retrieve(ResultSet rs, Connection con) throws RetrievalException
    {
        DbGate.getSharedInstance().load(this,rs,con);
    }

    public IEntityContext getContext()
    {
        return null;
    }
}