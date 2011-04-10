package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 12:43:17 PM
 */
public class RetrievalException extends BaseException
{
    public RetrievalException()
    {
    }

    public RetrievalException(String s)
    {
        super(s);
    }

    public RetrievalException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public RetrievalException(Throwable throwable)
    {
        super(throwable);
    }
}
