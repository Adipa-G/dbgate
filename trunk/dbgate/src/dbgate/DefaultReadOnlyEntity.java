package dbgate;

import dbgate.context.IEntityContext;
import dbgate.context.impl.EntityContext;
import dbgate.exceptions.RetrievalException;
import dbgate.ermanagement.ermapper.DbGate;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 9:27:01 PM
 */
public class DefaultReadOnlyEntity implements IReadOnlyEntity
{
    protected IEntityContext context;

    public DefaultReadOnlyEntity()
    {
        context = new EntityContext();
    }

    public void retrieve(ResultSet rs, Connection con) throws RetrievalException
    {
        DbGate.getSharedInstance().load(this,rs,con);
    }

    public IEntityContext getContext()
    {
        return context;
    }
}
