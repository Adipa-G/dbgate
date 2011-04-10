package dbgate.ermanagement.impl.utils;

import dbgate.DBClassStatus;
import dbgate.ServerDBClass;

/**
 * Date: Mar 24, 2011
 * Time: 10:15:25 PM
 */
public class MiscUtils
{
    public static void modify(ServerDBClass serverDBClass)
    {
        if (serverDBClass.getStatus() == DBClassStatus.UNMODIFIED)
        {
            serverDBClass.setStatus(DBClassStatus.MODIFIED);
        }
    }
}
