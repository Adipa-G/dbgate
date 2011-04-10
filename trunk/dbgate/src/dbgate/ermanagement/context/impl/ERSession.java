package dbgate.ermanagement.context.impl;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.context.IERSession;
import dbgate.ermanagement.context.IEntityFieldValueList;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:00:09 AM
 */
public class ERSession implements IERSession
{
    private Collection<IEntityFieldValueList> entityFieldValueList;

    public ERSession()
    {
        entityFieldValueList = new ArrayList<IEntityFieldValueList>();
    }

    @Override
    public Collection<IEntityFieldValueList> getProcessedObjects()
    {
        return entityFieldValueList;
    }

    @Override
    public ServerRODBClass getProcessed(ITypeFieldValueList typeKeyFieldList)
    {
        for (IEntityFieldValueList existingEntity : entityFieldValueList)
        {
            if (ERDataManagerUtils.isTypeKeyEquals(typeKeyFieldList,existingEntity))
            {
                return  existingEntity.getEntity();
            }
        }
        return null;
    }

    @Override
    public boolean isProcessed(ITypeFieldValueList typeKeyFieldList)
    {
        return getProcessed(typeKeyFieldList) != null;
    }

    @Override
    public void checkAndAddEntityList(IEntityFieldValueList entityKeyFieldList)
    {
        if (!isProcessed(entityKeyFieldList))
        {
            entityFieldValueList.add(entityKeyFieldList);
        }
    }
}
