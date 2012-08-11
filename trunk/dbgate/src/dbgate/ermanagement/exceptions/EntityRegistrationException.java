package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class EntityRegistrationException extends BaseException
{
    public EntityRegistrationException()
    {
    }

    public EntityRegistrationException(String s)
    {
        super(s);
    }

    public EntityRegistrationException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public EntityRegistrationException(Throwable throwable)
    {
        super(throwable);
    }
}
