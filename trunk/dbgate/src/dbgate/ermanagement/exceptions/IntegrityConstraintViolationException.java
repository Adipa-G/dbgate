package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 7:08:38 PM
 */
public class IntegrityConstraintViolationException extends BaseException
{
    public IntegrityConstraintViolationException()
    {
    }

    public IntegrityConstraintViolationException(String s)
    {
        super(s);
    }

    public IntegrityConstraintViolationException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public IntegrityConstraintViolationException(Throwable throwable)
    {
        super(throwable);
    }
}
