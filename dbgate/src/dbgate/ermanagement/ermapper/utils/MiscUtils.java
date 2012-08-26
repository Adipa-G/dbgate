package dbgate.ermanagement.ermapper.utils;

import dbgate.EntityStatus;
import dbgate.IEntity;

/**
 * Date: Mar 24, 2011
 * Time: 10:15:25 PM
 */
public class MiscUtils
{
    public static void modify(IEntity entity)
    {
        if (entity.getStatus() == EntityStatus.UNMODIFIED)
        {
            entity.setStatus(EntityStatus.MODIFIED);
        }
    }
}
