package dbgate.exceptions.common;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatementExecutionException extends DbGateException
{
    public StatementExecutionException(String s, Throwable throwable)
    {
        super(s, throwable);
    }
}
