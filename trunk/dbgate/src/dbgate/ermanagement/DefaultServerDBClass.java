package dbgate.ermanagement;

import dbgate.DBClassStatus;
import dbgate.ServerDBClass;
import dbgate.ermanagement.context.IEntityContext;
import dbgate.ermanagement.context.impl.EntityContext;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.ermanagement.impl.utils.MiscUtils;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 9:26:28 PM
 */
public class DefaultServerDBClass extends DefaultServerRODBClass implements ServerDBClass
{
    protected DBClassStatus status;

    public DefaultServerDBClass()
    {
        status = DBClassStatus.NEW;
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

    public void _modify()
    {
        MiscUtils.modify(this);
    }
}
