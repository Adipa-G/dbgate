package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.ermanagement.query.QueryFromExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSqlQueryFrom implements IAbstractQueryFrom
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
    public QueryFromExpressionType getFromExpressionType()
    {
        return QueryFromExpressionType.RAW_SQL;
    }

    @Override
    public String createSql()
    {
        return sql;
    }
}
