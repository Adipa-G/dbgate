package dbgate.support.persistant.inheritancetest;

import dbgate.EntityStatus;
import dbgate.context.IEntityContext;
import dbgate.context.impl.EntityContext;
import dbgate.support.persistant.treetest.ITreeTestOne2OneEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
public abstract class InheritanceTestSuperEntityExt implements ITreeTestOne2OneEntity
{
    private EntityContext context;
    private EntityStatus status;
    private int idCol;
    private String name;

    public InheritanceTestSuperEntityExt()
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

    public IEntityContext getContext()
    {
        return context;
    }
}