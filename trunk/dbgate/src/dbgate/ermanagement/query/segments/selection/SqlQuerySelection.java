package dbgate.ermanagement.query.segments.selection;

import dbgate.ermanagement.IQuerySelection;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.query.segments.SqlSegment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlQuerySelection extends SqlSegment implements IQuerySelection
{
    public SqlQuerySelection(String sql)
    {
        super(sql);
    }

    @Override
    public Object retrieve(ResultSet rs) throws SQLException
    {
        List<String> columns = Arrays.asList(sql.split("\\s*,\\s*"));
        while (columns.remove(""));

        Object[] readObjects = new Object[columns.size()];
        for (int i = 0, columnsLength = columns.size(); i < columnsLength; i++)
        {
            String column = columns.get(i);
            Object obj = rs.getObject(column);
            readObjects[i] = obj;
        }

        if (readObjects.length == 0)
            return readObjects[0];
        return readObjects;
    }
}
