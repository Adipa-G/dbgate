package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryFrom;
import dbgate.ermanagement.IQueryGroup;
import dbgate.ermanagement.query.segments.group.SqlQueryGroup;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryGroup
{
    public static SqlQueryGroup RawSql(String sql)
    {
        return new SqlQueryGroup(sql);
    }
}
