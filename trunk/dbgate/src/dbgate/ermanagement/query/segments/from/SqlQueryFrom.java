package dbgate.ermanagement.query.segments.from;

import dbgate.ermanagement.IQueryFrom;
import dbgate.ermanagement.query.segments.SqlSegment;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlQueryFrom extends SqlSegment implements IQueryFrom
{
    public SqlQueryFrom(String sql)
    {
        super(sql);
    }
}
