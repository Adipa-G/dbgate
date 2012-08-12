package dbgate.exceptions.common;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatementPreparingException extends DbGateException
{
    public StatementPreparingException()
    {
    }

    public StatementPreparingException(String s)
    {
        super(s);
    }

    public StatementPreparingException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public StatementPreparingException(Throwable throwable)
    {
        super(throwable);
    }
}
