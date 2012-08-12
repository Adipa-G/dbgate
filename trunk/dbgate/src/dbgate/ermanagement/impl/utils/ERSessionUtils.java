package dbgate.ermanagement.impl.utils;

import dbgate.IReadOnlyEntity;
import dbgate.ermanagement.context.IEntityFieldValueList;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.context.impl.ERSession;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Apr 3, 2011
 * Time: 2:58:38 PM
 */
public class ERSessionUtils
{
    public static void initSession(IReadOnlyEntity roEntity)
    {
        if (roEntity.getContext() != null
                && roEntity.getContext().getERSession() == null)
        {
            roEntity.getContext().setERSession(new ERSession());
        }
    }

    public static void transferSession(IReadOnlyEntity parentEntity,IReadOnlyEntity childEntity)
    {
        if (parentEntity.getContext() != null
                && childEntity.getContext() != null
                && parentEntity.getContext().getERSession() != null)
        {
            childEntity.getContext().setERSession(parentEntity.getContext().getERSession());
        }
    }

    public static void destroySession(IReadOnlyEntity roEntity)
    {
        if (roEntity.getContext() != null
                && roEntity.getContext().getERSession() != null)
        {
            roEntity.getContext().setERSession(null);
        }
    }

    public static boolean existsInSession(IReadOnlyEntity roEntity, ITypeFieldValueList typeList)
    {
        if (roEntity.getContext() != null
                && roEntity.getContext().getERSession() != null
                && typeList != null)
        {
            return roEntity.getContext().getERSession().isProcessed(typeList);
        }
        return false;
    }

    public static void addToSession(IReadOnlyEntity roEntity, IEntityFieldValueList typeList)
    {
        if (roEntity.getContext() != null
                && roEntity.getContext().getERSession() != null
                && typeList != null)
        {
            roEntity.getContext().getERSession().checkAndAddEntityList(typeList);
        }
    }

    public static IReadOnlyEntity getFromSession(IReadOnlyEntity roEntity, ITypeFieldValueList typeList)
    {
        if (roEntity.getContext() != null
                && roEntity.getContext().getERSession() != null
                && typeList != null)
        {
            return roEntity.getContext().getERSession().getProcessed(typeList);
        }
        return null;
    }
}
