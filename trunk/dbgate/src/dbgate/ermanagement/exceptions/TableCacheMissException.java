package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 12:40:40 PM
 */
public class TableCacheMissException extends BaseException
{
    public TableCacheMissException()
    {
    }

    public TableCacheMissException(String s)
    {
        super(s);
    }

    public TableCacheMissException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public TableCacheMissException(Throwable throwable)
    {
        super(throwable);
    }
}
