package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryFrom;
import dbgate.ermanagement.query.segments.from.SqlQueryFrom;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryFrom
{
    public static SqlQueryFrom RawSql(String sql)
    {
        return new SqlQueryFrom(sql);
    }
}
