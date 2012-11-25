package dbgate.exceptions.common;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/26/12
 * Time: 6:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionCreationFailedException extends DbGateException
{
    public TransactionCreationFailedException(String s, Throwable throwable)
    {
        super(s, throwable);
    }
}
