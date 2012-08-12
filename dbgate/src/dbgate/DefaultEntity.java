package dbgate;

import dbgate.exceptions.PersistException;
import dbgate.ermanagement.ermapper.DbGate;
import dbgate.ermanagement.ermapper.utils.MiscUtils;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 9:26:28 PM
 */
public class DefaultEntity extends DefaultReadOnlyEntity implements IEntity
{
    protected EntityStatus status;

    public DefaultEntity()
    {
        status = EntityStatus.NEW;
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

    public void _modify()
    {
        MiscUtils.modify(this);
    }
}
