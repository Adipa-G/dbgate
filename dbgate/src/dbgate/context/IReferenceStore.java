package dbgate.context;

import dbgate.DbGateException;
import dbgate.IReadOnlyEntity;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:05:32 AM
 */
public interface IReferenceStore
{
    boolean alreadyInCurrentObjectGraph(ITypeFieldValueList keys);

    IReadOnlyEntity getFromCurrentObjectGraph(ITypeFieldValueList keys);

    void addToCurrentObjectGraphIndex(IReadOnlyEntity refEntity) throws DbGateException;
}
