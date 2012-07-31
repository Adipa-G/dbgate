package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ermanagement.IQuerySelection;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecInfo;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecParam;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QuerySelectionExpressionType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSqlQuerySelection implements IAbstractQuerySelection
{
    protected String sql;


    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql = sql;
    }

    @Override
    public QuerySelectionExpressionType getSelectionType()
    {
        return QuerySelectionExpressionType.RAW_SQL;
    }

    @Override
    public String createSql(QueryBuildInfo buildInfo)
    {
        return sql;
    }

    public Object retrieve(ResultSet rs,Connection con) throws RetrievalException
    {
        try
        {
            List<String> columns = Arrays.asList(sql.split("\\s*,\\s*"));
            while (columns.remove(""));

            Object[] readObjects = new Object[columns.size()];
            for (int i = 0, columnsLength = columns.size(); i < columnsLength; i++)
            {
                String column = columns.get(i).toLowerCase();
                if (column.contains(" as "))
                {
                    column = column.split("as")[1].trim();
                }
                Object obj = rs.getObject(column);
                readObjects[i] = obj;
            }

            if (readObjects.length == 0)
                return readObjects[0];
            return readObjects;
        }
        catch (Exception ex)
        {
            throw new RetrievalException(ex.getMessage(),ex);
        }
    }
}
