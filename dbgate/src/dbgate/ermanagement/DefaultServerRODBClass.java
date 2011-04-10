package dbgate.ermanagement;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.context.IEntityContext;
import dbgate.ermanagement.context.impl.EntityContext;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.ERLayer;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 9:27:01 PM
 */
public class DefaultServerRODBClass implements ServerRODBClass
{
    protected IEntityContext context;

    public DefaultServerRODBClass()
    {
        context = new EntityContext();
    }

    public void retrieve(ResultSet rs, Connection con) throws RetrievalException
    {
        ERLayer.getSharedInstance().load(this,rs,con);
    }

    public IEntityContext getContext()
    {
        return context;
    }
}
