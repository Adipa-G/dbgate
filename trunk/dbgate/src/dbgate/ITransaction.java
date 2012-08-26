package dbgate;

import dbgate.exceptions.common.TransactionCloseFailedException;
import dbgate.exceptions.common.TransactionCommitFailedException;
import dbgate.exceptions.common.TransactionRollbackFailedException;

import java.sql.Connection;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/26/12
 * Time: 6:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITransaction
{
    ITransactionFactory getFactory();

    UUID getTransactionId();

    Connection getConnection();

    IDbGate getDbGate();

    boolean isClosed();

    void commit() throws TransactionCommitFailedException;

    void rollBack() throws TransactionRollbackFailedException;

    void close() throws TransactionCloseFailedException;
}
