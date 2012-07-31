package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join;

import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby.IAbstractQueryOrderBy;
import dbgate.ermanagement.query.QueryJoinExpressionType;
import dbgate.ermanagement.query.QueryOrderByExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSqlQueryJoin implements IAbstractQueryJoin
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
    public QueryJoinExpressionType getJoinExpressionType()
    {
        return QueryJoinExpressionType.RAW_SQL;
    }

    @Override
    public String createSql()
    {
        return sql;
    }
}
