package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.DateWrapper;
import dbgate.TimeStampWrapper;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 6:50:01 PM
 */
public class OracleDataManipulate extends AbstractDataManipulate
{
    public OracleDataManipulate(IDBLayer dbLayer)
    {
        super(dbLayer);
    }

    @Override
    public ResultSet createResultSet(Connection con, String sql, int[] types, Object[] values) throws SQLException
    {
        boolean storedProcedure = isStoredProcedure(sql);

        PreparedStatement ps;
        ResultSet rs;

        if (storedProcedure)
        {
            ps = con.prepareCall(sql);
        }
        else
        {
            ps = con.prepareStatement(sql);
        }

        for (int i = 0; i < (storedProcedure ? types.length + 1 : types.length); i++)
        {
            int count = i + 1;
            if (i == 0 && storedProcedure)
            {
                ((CallableStatement) ps).registerOutParameter(count, OracleTypes.CURSOR);
                continue;
            }
            int type = storedProcedure ? types[i - 1] : types[i];
            Object value = storedProcedure ? values[i - 1] : values[i];

            if (value == null)
            {
                ps.setNull(count, type);
            }
            else
            {
                switch (type)
                {
                    case Types.BIGINT:
                    case Types.NUMERIC:
                        ps.setLong(count, (Long) value);
                        break;
                    case Types.BOOLEAN:
                        ps.setBoolean(count, (Boolean) value);
                        break;
                    case Types.CHAR:
                        ps.setInt(count, (Character) value);
                        break;
                    case Types.INTEGER:
                        ps.setInt(count, (Integer) value);
                        break;
                    case Types.DATE:
                        ps.setDate(count, ((DateWrapper) value)._getSQLDate());
                        break;
                    case Types.DOUBLE:
                        ps.setDouble(count, (Double) value);
                        break;
                    case Types.FLOAT:
                        ps.setFloat(count, (Float) value);
                        break;
                    case Types.TIMESTAMP:
                        ps.setTimestamp(count, ((TimeStampWrapper) value)._getSQLTimeStamp());
                        break;
                    case Types.VARCHAR:
                        ps.setString(count, (String) value);
                        break;
                }
            }
        }

        if (storedProcedure)
        {
            ps.execute();
            rs = ((OracleCallableStatement) ps).getCursor(1);
        }
        else
        {
            rs = ps.executeQuery();
        }
        return rs;
    }

    private static  boolean isStoredProcedure(String sql)
    {
        return sql.replaceAll(" ", "").contains("begin?:=");
    }
}
