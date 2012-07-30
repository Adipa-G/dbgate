package dbgate.ermanagement.query.segments.group;

import dbgate.ermanagement.IQueryFrom;
import dbgate.ermanagement.IQueryGroup;
import dbgate.ermanagement.query.segments.SqlSegment;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlQueryGroup extends SqlSegment implements IQueryGroup
{
    public SqlQueryGroup(String sql)
    {
        super(sql);
    }
}
