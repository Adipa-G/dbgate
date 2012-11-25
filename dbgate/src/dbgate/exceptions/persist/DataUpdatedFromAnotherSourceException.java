package dbgate.exceptions.persist;

import dbgate.DbGateException;

/**
 * Date: Mar 28, 2011
 * Time: 10:05:55 PM
 */
public class DataUpdatedFromAnotherSourceException extends DbGateException
{
    public DataUpdatedFromAnotherSourceException(String s)
    {
        super(s);
    }
}
