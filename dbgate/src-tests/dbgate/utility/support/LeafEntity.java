package dbgate.utility.support;

import dbgate.DBClassStatus;
import dbgate.IDBClass;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 9:27:47 AM
 */
public class LeafEntity implements IDBClass
{
    private DBClassStatus status;
    private RootEntity rootEntity;

    public RootEntity getRootEntity()
    {
        return rootEntity;
    }

    public void setRootEntity(RootEntity rootEntity)
    {
        this.rootEntity = rootEntity;
    }

    public DBClassStatus getStatus()
    {
        return status;
    }

    public void setStatus(DBClassStatus status)
    {
        this.status = status;
    }
}
