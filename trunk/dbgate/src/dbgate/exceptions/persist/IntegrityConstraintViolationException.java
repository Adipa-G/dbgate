package dbgate.exceptions.persist;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 7:08:38 PM
 */
public class IntegrityConstraintViolationException extends DbGateException
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
