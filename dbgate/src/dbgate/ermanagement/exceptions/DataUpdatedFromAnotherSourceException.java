package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Date: Mar 28, 2011
 * Time: 10:05:55 PM
 */
public class DataUpdatedFromAnotherSourceException extends BaseException
{
    public DataUpdatedFromAnotherSourceException()
    {
    }

    public DataUpdatedFromAnotherSourceException(String s)
    {
        super(s);
    }

    public DataUpdatedFromAnotherSourceException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public DataUpdatedFromAnotherSourceException(Throwable throwable)
    {
        super(throwable);
    }
}
