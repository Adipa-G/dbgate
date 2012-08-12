package dbgate.exceptions.common;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 7:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityInstantiationException extends DbGateException
{
    public EntityInstantiationException()
    {
    }

    public EntityInstantiationException(String s)
    {
        super(s);
    }

    public EntityInstantiationException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public EntityInstantiationException(Throwable throwable)
    {
        super(throwable);
    }
}
