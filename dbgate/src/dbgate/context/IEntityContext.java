package dbgate.context;

import dbgate.DbGateException;
import dbgate.IReadOnlyEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:04:07 AM
 */
public interface IEntityContext
{
    IChangeTracker getChangeTracker();

    IReferenceStore getReferenceStore();

    void destroyReferenceStore();

    void copyReferenceStoreFrom(IReadOnlyEntity entity);

    boolean alreadyInCurrentObjectGraph(ITypeFieldValueList keys);

    IReadOnlyEntity getFromCurrentObjectGraph(ITypeFieldValueList keys);

    void addToCurrentObjectGraphIndex(IReadOnlyEntity refEntity) throws DbGateException;
}
