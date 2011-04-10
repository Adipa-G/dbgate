package dbgate.dbutility;

import dbgate.GeneralLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 30, 2006
 * Time: 11:52:10 PM
 * ----------------------------------------
 */
public class DBConnector
{
    public static final int DB_ORACLE = 1;
    public static final int DB_POSTGRE = 2;
    public static final int DB_ACCESS = 3;
    public static final int DB_SQLLITE = 4;
    public static final int DB_DERBY = 5;
    public static final int DB_MYSQL = 6;

    private static DBConnector staticInstance;
    private int dbType;

    private String connectionString;
    private String className;

    public DBConnector(String connectionString,String className,int dbType) throws SQLException
    {
        this.connectionString = connectionString;
        this.className = className;
        this.dbType = dbType;
        staticInstance = this;
    }

    public Connection getConnection()
    {
        Connection conn = null;
        try
        {
            Class.forName(className);
            conn = DriverManager.getConnection(connectionString);
        }
        catch (Exception ex)
        {
            GeneralLogger.getLogger().log(Level.SEVERE,ex.getMessage(),ex);
        }
        return conn;
    }

    public static DBConnector getSharedInstance()
    {
        return staticInstance;
    }

    public int getDbType()
    {
        return dbType;
    }

    public void setDbType(int dbType)
    {
        this.dbType = dbType;
    }

    public void finalize()
    {
        GeneralLogger.getLogger().info("Finalizing Pool");
        try
        {
            super.finalize();
            staticInstance = null;
        }
        catch (Throwable ex)
        {
            GeneralLogger.getLogger().log(Level.SEVERE,ex.getMessage(),ex);
        }
    }
}
