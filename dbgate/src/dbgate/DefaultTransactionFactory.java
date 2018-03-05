package dbgate;

import dbgate.ermanagement.ermapper.DbGate;
import dbgate.ermanagement.ermapper.Transaction;
import dbgate.exceptions.common.TransactionCreationFailedException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 30, 2006
 * Time: 11:52:10 PM
 * ----------------------------------------
 */
public class DefaultTransactionFactory implements ITransactionFactory
{
    public static final int DB_ORACLE = 1;
    public static final int DB_POSTGRE = 2;
    public static final int DB_ACCESS = 3;
    public static final int DB_SQLLITE = 4;
    public static final int DB_DERBY = 5;
    public static final int DB_MYSQL = 6;

    private Supplier<Connection> connectionFactory;
    private IDbGate dbGate;

    public DefaultTransactionFactory(Supplier<Connection> connectionFactory,
                                     int dbType) throws SQLException
    {
        this.connectionFactory = connectionFactory;
        this.dbGate = new DbGate(dbType);
    }

    @Override
    public ITransaction createTransaction() throws TransactionCreationFailedException
    {
        try
        {
            Connection conn = this.connectionFactory.get();
            return new Transaction(this,conn);
        }
        catch (Exception ex)
        {
            throw new TransactionCreationFailedException("Failed to create a transaction",ex);
        }
    }

    @Override
    public IDbGate getDbGate()
    {
        return dbGate;
    }

    public void finalize()
    {
        GeneralLogger.getLogger().info("Finalizing transaction factory");
        try
        {
            super.finalize();
        }
        catch (Throwable ex)
        {
            GeneralLogger.getLogger().log(Level.SEVERE,ex.getMessage(),ex);
        }
    }
}
