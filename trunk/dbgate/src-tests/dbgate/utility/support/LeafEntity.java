package dbgate.utility.support;

import dbgate.EntityStatus;
import dbgate.IClientEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 9:27:47 AM
 */
public class LeafEntity implements IClientEntity
{
    private EntityStatus status;
    private RootEntity rootEntity;

    public RootEntity getRootEntity()
    {
        return rootEntity;
    }

    public void setRootEntity(RootEntity rootEntity)
    {
        this.rootEntity = rootEntity;
    }

    public EntityStatus getStatus()
    {
        return status;
    }

    public void setStatus(EntityStatus status)
    {
        this.status = status;
    }
}
