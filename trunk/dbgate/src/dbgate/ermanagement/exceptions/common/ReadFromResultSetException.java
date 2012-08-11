package dbgate.ermanagement.exceptions.common;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 7:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadFromResultSetException extends DbGateException
{
    public ReadFromResultSetException()
    {
    }

    public ReadFromResultSetException(String s)
    {
        super(s);
    }

    public ReadFromResultSetException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public ReadFromResultSetException(Throwable throwable)
    {
        super(throwable);
    }
}
