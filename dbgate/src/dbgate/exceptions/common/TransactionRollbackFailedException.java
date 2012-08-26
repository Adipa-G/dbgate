package dbgate.exceptions.common;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/26/12
 * Time: 6:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionRollbackFailedException extends DbGateException
{
    public TransactionRollbackFailedException()
    {
    }

    public TransactionRollbackFailedException(String s)
    {
        super(s);
    }

    public TransactionRollbackFailedException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public TransactionRollbackFailedException(Throwable throwable)
    {
        super(throwable);
    }
}
