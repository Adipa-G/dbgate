package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 12:16:56 PM
 */
public class NoFieldsFoundException extends BaseException
{
    public NoFieldsFoundException()
    {
    }

    public NoFieldsFoundException(String s)
    {
        super(s);
    }

    public NoFieldsFoundException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public NoFieldsFoundException(Throwable throwable)
    {
        super(throwable);
    }
}
