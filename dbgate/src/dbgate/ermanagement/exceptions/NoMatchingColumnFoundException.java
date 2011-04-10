package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 5:31:27 PM
 */
public class NoMatchingColumnFoundException extends BaseException
{
    public NoMatchingColumnFoundException()
    {
    }

    public NoMatchingColumnFoundException(String s)
    {
        super(s);
    }

    public NoMatchingColumnFoundException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public NoMatchingColumnFoundException(Throwable throwable)
    {
        super(throwable);
    }
}
