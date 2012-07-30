package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryCondition;
import dbgate.ermanagement.query.segments.condition.SqlQueryCondition;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/26/12
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryCondition
{
    public static SqlQueryCondition RawSql(String sql)
    {
        return new SqlQueryCondition(sql);
    }
}
