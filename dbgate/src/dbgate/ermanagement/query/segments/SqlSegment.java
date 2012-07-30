package dbgate.ermanagement.query.segments;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlSegment
{
    protected String sql;

    public SqlSegment(String sql)
    {
        this.sql = sql;
    }

    public String getSql()
    {
        return sql;
    }
}
