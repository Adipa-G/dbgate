package dbgate.ermanagement.context.impl;

import dbgate.ermanagement.context.IChangeTracker;
import dbgate.ermanagement.context.IERSession;
import dbgate.ermanagement.context.IEntityContext;

/**
 * Date: Mar 23, 2011
 * Time: 9:27:09 PM
 */
public class EntityContext implements IEntityContext
{
    private IChangeTracker changeTracker;
    private IERSession erSession;

    public EntityContext()
    {
        changeTracker = new ChangeTracker();
    }

    @Override
    public IChangeTracker getChangeTracker()
    {
        return changeTracker;
    }

    @Override
    public IERSession getERSession()
    {
        return erSession;
    }

    @Override
    public void setERSession(IERSession erSession)
    {
        this.erSession = erSession;
    }
}
