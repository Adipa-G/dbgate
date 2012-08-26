package dbgate;

import dbgate.ermanagement.ermapper.utils.MiscUtils;
import dbgate.exceptions.PersistException;

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

    public void persist(ITransaction tx) throws PersistException
    {
        tx.getDbGate().save(this,tx);
    }

    public void _modify()
    {
        MiscUtils.modify(this);
    }
}
