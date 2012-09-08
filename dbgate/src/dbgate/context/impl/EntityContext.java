package dbgate.context.impl;

import dbgate.DbGateException;
import dbgate.IReadOnlyEntity;
import dbgate.context.*;

/**
 * Date: Mar 23, 2011
 * Time: 9:27:09 PM
 */
public class EntityContext implements IEntityContext
{
    private IChangeTracker changeTracker;
    private IReferenceStore referenceStore;

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
    public IReferenceStore getReferenceStore()
    {
        initReferenceStore();
        return referenceStore;
    }

    @Override
    public void destroyReferenceStore()
    {
        referenceStore = null;
    }

    @Override
    public void copyReferenceStoreFrom(IReadOnlyEntity entity)
    {
        initReferenceStore();
        if (entity.getContext() != null)
            referenceStore = entity.getContext().getReferenceStore();
    }

    @Override
    public boolean alreadyInCurrentObjectGraph(ITypeFieldValueList keys)
    {
        return referenceStore != null && referenceStore.alreadyInCurrentObjectGraph(keys);
    }

    @Override
    public IReadOnlyEntity getFromCurrentObjectGraph(ITypeFieldValueList keys)
    {
        if (referenceStore == null)
            return null;
        return referenceStore.getFromCurrentObjectGraph(keys);
    }

    @Override
    public void addToCurrentObjectGraphIndex(IReadOnlyEntity refEntity) throws DbGateException
    {
        initReferenceStore();
        referenceStore.addToCurrentObjectGraphIndex(refEntity);
    }

    private void initReferenceStore()
    {
        if (referenceStore == null)
            referenceStore = new ReferenceStore();
    }
}
