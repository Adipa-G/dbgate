package dbgate.exceptions.migration;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 30, 2006
 * Time: 7:37:31 PM
 */
public class MetaDataException extends DbGateException
{
    public MetaDataException(String reason)
    {
        super(reason);
    }

    public MetaDataException()
    {
    }

    public MetaDataException(Throwable cause)
    {
        super(cause);
    }

    public MetaDataException(String reason, Throwable cause)
    {
        super(reason, cause);
    }
}
