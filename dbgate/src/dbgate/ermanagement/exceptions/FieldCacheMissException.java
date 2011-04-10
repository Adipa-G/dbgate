package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 12:19:26 PM
 */
public class FieldCacheMissException extends BaseException
{
    public FieldCacheMissException()
    {
    }

    public FieldCacheMissException(String s)
    {
        super(s);
    }

    public FieldCacheMissException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public FieldCacheMissException(Throwable throwable)
    {
        super(throwable);
    }
}
