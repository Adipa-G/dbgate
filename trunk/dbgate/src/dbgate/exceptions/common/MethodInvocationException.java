package dbgate.exceptions.common;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 7:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodInvocationException extends DbGateException
{
    public MethodInvocationException(String s, Throwable throwable)
    {
        super(s, throwable);
    }
}
