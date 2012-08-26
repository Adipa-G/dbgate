package dbgate;

import dbgate.exceptions.common.TransactionCreationFailedException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/26/12
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITransactionFactory
{
    ITransaction createTransaction() throws TransactionCreationFailedException;

    IDbGate getDbGate();
}
