package dbgate.exceptions.common;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 7:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodNotFoundException extends DbGateException
{
    public MethodNotFoundException()
    {
    }

    public MethodNotFoundException(String s)
    {
        super(s);
    }

    public MethodNotFoundException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public MethodNotFoundException(Throwable throwable)
    {
        super(throwable);
    }
}
