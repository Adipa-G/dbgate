package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.dbdm.oracledm;

import dbgate.ColumnType;
import dbgate.ermanagement.exceptions.common.StatementExecutionException;
import dbgate.ermanagement.exceptions.common.StatementPreparingException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.AbstractDataManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecInfo;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecParam;
import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

import java.sql.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    public ResultSet createResultSet(Connection con, final QueryExecInfo execInfo)
        throws StatementPreparingException,StatementExecutionException
    {
        try
        {
            boolean storedProcedure = isStoredProcedure(execInfo.getSql());

            PreparedStatement ps;
            ResultSet rs;

            if (storedProcedure)
            {
                ps = con.prepareCall(execInfo.getSql());
            }
            else
            {
                ps = con.prepareStatement(execInfo.getSql());
            }

            List<QueryExecParam> params = execInfo.getParams();
            Collections.sort(params, new Comparator<QueryExecParam>()
            {
                @Override
                public int compare(QueryExecParam o1, QueryExecParam o2)
                {
                    return (new Integer(o1.getIndex())).compareTo(o2.getIndex());
                }
            });

            for (int i = 0; i < (storedProcedure ? params.size() + 1 : params.size()); i++)
            {
                int count = i + 1;
                if (i == 0 && storedProcedure)
                {
                    ((CallableStatement) ps).registerOutParameter(count, OracleTypes.CURSOR);
                    continue;
                }

                QueryExecParam param = storedProcedure ? params.get(i - 1) : params.get(i);
                ColumnType type = param.getType();
                Object value = param.getValue();

                setToPreparedStatement(ps,value,count,value == null,type);
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
        catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying set executing %s",execInfo.getSql());
            throw new StatementExecutionException(message,ex);
        }
    }

    private static  boolean isStoredProcedure(String sql)
    {
        return sql.replaceAll(" ", "").contains("begin?:=");
    }
}
