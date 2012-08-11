package dbgate.ermanagement.exceptions.persist;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 5:58:11 PM
 */
public class IncorrectStatusException extends DbGateException
{
    public IncorrectStatusException()
    {
    }

    public IncorrectStatusException(String s)
    {
        super(s);
    }

    public IncorrectStatusException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public IncorrectStatusException(Throwable throwable)
    {
        super(throwable);
    }
}
