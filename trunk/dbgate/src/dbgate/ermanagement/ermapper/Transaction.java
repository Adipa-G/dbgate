package dbgate.ermanagement.ermapper;

import dbgate.GeneralLogger;
import dbgate.IDbGate;
import dbgate.ITransaction;
import dbgate.ITransactionFactory;
import dbgate.exceptions.common.TransactionCloseFailedException;
import dbgate.exceptions.common.TransactionCommitFailedException;
import dbgate.exceptions.common.TransactionRollbackFailedException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/26/12
 * Time: 6:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class Transaction implements ITransaction
{
    private UUID transactionId;
    private IDbGate dbGate;

    private ITransactionFactory factory;
    private Connection connection;

    public Transaction(ITransactionFactory factory, Connection connection)
    {
        this.transactionId = UUID.randomUUID();
        this.factory = factory;
        this.connection = connection;
        this.dbGate = factory.getDbGate();
    }

    @Override
    public ITransactionFactory getFactory()
    {
        return factory;
    }

    @Override
    public UUID getTransactionId()
    {
        return transactionId;
    }

    @Override
    public Connection getConnection()
    {
        return connection;
    }

    @Override
    public IDbGate getDbGate()
    {
        return dbGate;
    }

    @Override
    public boolean isClosed()
    {
        try
        {
            return connection != null
                    && connection.isClosed();
        } 
        catch (SQLException e)
        {
            GeneralLogger.getLogger().log(Level.SEVERE,e.getMessage(),e);
        }
        return true;
    }

    @Override
    public void commit() throws TransactionCommitFailedException
    {
        try
        {
            connection.commit();
        }
        catch (SQLException e)
        {
            throw new TransactionCommitFailedException(String.format("Unable to commit the transaction %s"
                    ,transactionId.toString()),e);
        }
    }

    @Override
    public void rollBack() throws TransactionRollbackFailedException
    {
        try
        {
            connection.rollback();
        }
        catch (SQLException e)
        {
            throw new TransactionRollbackFailedException(String.format("Unable to rollback the transaction %s"
                    ,transactionId.toString()),e);
        }
    }

    @Override
    public void close() throws TransactionCloseFailedException
    {
        try
        {
            factory = null;
            connection.close();
        }
        catch (SQLException e)
        {
            throw new TransactionCloseFailedException(String.format("Unable to close the transaction %s"
                    ,transactionId.toString()),e);
        }
    }
}
