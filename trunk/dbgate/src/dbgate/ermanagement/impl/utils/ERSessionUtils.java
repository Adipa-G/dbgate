package dbgate.ermanagement.impl.utils;

import dbgate.ServerRODBClass;
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
    public static void initSession(ServerRODBClass roEntity)
    {
        if (roEntity.getContext() != null
                && roEntity.getContext().getERSession() == null)
        {
            roEntity.getContext().setERSession(new ERSession());
        }
    }

    public static void transferSession(ServerRODBClass parentEntity,ServerRODBClass childEntity)
    {
        if (parentEntity.getContext() != null
                && childEntity.getContext() != null
                && parentEntity.getContext().getERSession() != null)
        {
            childEntity.getContext().setERSession(parentEntity.getContext().getERSession());
        }
    }

    public static void destroySession(ServerRODBClass roEntity)
    {
        if (roEntity.getContext() != null
                && roEntity.getContext().getERSession() != null)
        {
            roEntity.getContext().setERSession(null);
        }
    }

    public static boolean existsInSession(ServerRODBClass roEntity, ITypeFieldValueList typeList)
    {
        if (roEntity.getContext() != null
                && roEntity.getContext().getERSession() != null
                && typeList != null)
        {
            return roEntity.getContext().getERSession().isProcessed(typeList);
        }
        return false;
    }

    public static void addToSession(ServerRODBClass roEntity, IEntityFieldValueList typeList)
    {
        if (roEntity.getContext() != null
                && roEntity.getContext().getERSession() != null
                && typeList != null)
        {
            roEntity.getContext().getERSession().checkAndAddEntityList(typeList);
        }
    }

    public static ServerRODBClass getFromSession(ServerRODBClass roEntity, ITypeFieldValueList typeList)
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
