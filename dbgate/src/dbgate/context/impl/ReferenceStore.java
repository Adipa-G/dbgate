package dbgate.context.impl;

import dbgate.DbGateException;
import dbgate.IReadOnlyEntity;
import dbgate.context.IReferenceStore;
import dbgate.context.IEntityFieldValueList;
import dbgate.context.ITypeFieldValueList;
import dbgate.ermanagement.ermapper.utils.OperationUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:00:09 AM
 */
public class ReferenceStore implements IReferenceStore
{
    private Collection<IEntityFieldValueList> entityFieldValueList;

    public ReferenceStore()
    {
        entityFieldValueList = new ArrayList<>();
    }

    @Override
    public IReadOnlyEntity getFromCurrentObjectGraph(ITypeFieldValueList keys)
    {
        for (IEntityFieldValueList existingEntity : entityFieldValueList)
        {
            if (OperationUtils.isTypeKeyEquals(keys, existingEntity))
            {
                return  existingEntity.getEntity();
            }
        }
        return null;
    }

    @Override
    public boolean alreadyInCurrentObjectGraph(ITypeFieldValueList keys)
    {
        return getFromCurrentObjectGraph(keys) != null;
    }

    @Override
    public void addToCurrentObjectGraphIndex(IReadOnlyEntity refEntity) throws DbGateException
    {
        IEntityFieldValueList refKeyList = OperationUtils.extractEntityKeyValues(refEntity);
        if (!alreadyInCurrentObjectGraph(refKeyList))
        {
            entityFieldValueList.add(refKeyList);
        }
    }
}
