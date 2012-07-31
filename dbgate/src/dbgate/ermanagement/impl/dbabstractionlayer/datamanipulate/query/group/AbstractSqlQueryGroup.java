package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group;

import dbgate.ermanagement.query.QueryGroupExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSqlQueryGroup implements IAbstractQueryGroup
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
    public QueryGroupExpressionType getGroupExpressionType()
    {
        return QueryGroupExpressionType.RAW_SQL;
    }

    @Override
    public String createSql()
    {
        return sql;
    }
}
