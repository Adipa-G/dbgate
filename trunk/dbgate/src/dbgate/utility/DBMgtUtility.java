package dbgate.utility;

import dbgate.GeneralLogger;

import java.sql.*;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jul 28, 2007
 * Time: 10:11:19 AM
 * ----------------------------------------
 */
public class DBMgtUtility
{
    public static void close(Connection con)
    {
        if (con != null)
        {
            try
            {
                if (!con.isClosed())
                {
                    con.close();
                }
            }
            catch (SQLException ex)
            {
                GeneralLogger.getLogger().log(Level.SEVERE,ex.getMessage(),ex);
            }
        }
    }

    public static void close(PreparedStatement ps)
    {
        if (ps != null)
        {
            try
            {
                ps.close();
            }
            catch (SQLException ex)
            {
                GeneralLogger.getLogger().log(Level.SEVERE,ex.getMessage(),ex);
            }
        }
    }

    public static void close(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {
                GeneralLogger.getLogger().log(Level.SEVERE,ex.getMessage(),ex);
            }
        }
    }

    public static void close(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (SQLException ex)
            {
                GeneralLogger.getLogger().log(Level.SEVERE,ex.getMessage(),ex);
            }
        }
    }
}
