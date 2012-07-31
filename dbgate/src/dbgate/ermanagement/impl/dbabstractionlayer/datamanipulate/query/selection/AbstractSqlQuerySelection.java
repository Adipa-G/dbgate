package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ermanagement.query.QuerySelectionType;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    public QuerySelectionType getSelectionType()
    {
        return QuerySelectionType.RAW_SQL;
    }

    @Override
    public String createSql()
    {
        return sql;
    }

    public Object retrieve(ResultSet rs) throws SQLException
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
}
