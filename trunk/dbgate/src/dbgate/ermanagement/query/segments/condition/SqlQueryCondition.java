package dbgate.ermanagement.query.segments.condition;

import dbgate.ermanagement.IQueryCondition;
import dbgate.ermanagement.query.segments.SqlSegment;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlQueryCondition extends SqlSegment implements IQueryCondition
{
    public SqlQueryCondition(String sql)
    {
        super(sql);
    }
}
