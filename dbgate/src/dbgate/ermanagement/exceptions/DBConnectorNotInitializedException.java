package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 12:11:15 PM
 */
public class DBConnectorNotInitializedException extends BaseException
{
    public DBConnectorNotInitializedException()
    {
    }

    public DBConnectorNotInitializedException(String s)
    {
        super(s);
    }

    public DBConnectorNotInitializedException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public DBConnectorNotInitializedException(Throwable throwable)
    {
        super(throwable);
    }
}
