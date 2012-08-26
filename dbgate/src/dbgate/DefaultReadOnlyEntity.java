package dbgate;

import dbgate.context.IEntityContext;
import dbgate.context.impl.EntityContext;
import dbgate.exceptions.RetrievalException;

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

    public void retrieve(ResultSet rs, ITransaction tx) throws RetrievalException
    {
        tx.getDbGate().load(this,rs,tx);
    }

    public IEntityContext getContext()
    {
        return context;
    }
}
