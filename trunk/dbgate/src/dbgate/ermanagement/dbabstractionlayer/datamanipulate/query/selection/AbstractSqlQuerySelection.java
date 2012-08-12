package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.exceptions.ExpressionParsingException;
import dbgate.exceptions.RetrievalException;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QuerySelectionExpressionType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSqlQuerySelection implements IAbstractSelection
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
    public String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        return sql;
    }

    public Object retrieve(ResultSet rs,Connection con,QueryBuildInfo buildInfo) throws RetrievalException
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

            if (readObjects.length == 1)
                return readObjects[0];
            return readObjects;
        }
        catch (Exception ex)
        {
            throw new RetrievalException(ex.getMessage(),ex);
        }
    }
}
