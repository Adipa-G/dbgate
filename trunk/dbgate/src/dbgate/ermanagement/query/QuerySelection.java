package dbgate.ermanagement.query;

import dbgate.ermanagement.IQuerySelection;
import dbgate.ermanagement.query.segments.selection.SqlQuerySelection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuerySelection
{
    public static SqlQuerySelection RawSql(String sql)
    {
        return new SqlQuerySelection(sql);
    }
}
