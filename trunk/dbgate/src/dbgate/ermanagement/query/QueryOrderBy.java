package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryJoin;
import dbgate.ermanagement.IQueryOrderBy;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby.AbstractQueryOrderByFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby.AbstractSqlQueryOrderBy;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryOrderBy
{
    private static AbstractQueryOrderByFactory factory;

    public static void setFactory(AbstractQueryOrderByFactory f)
    {
        factory = f;
    }

    public static IQueryOrderBy rawSql(String sql)
    {
        AbstractSqlQueryOrderBy queryOrderBy = (AbstractSqlQueryOrderBy) factory.createOrderBy(QueryOrderByExpressionType.RAW_SQL);
        queryOrderBy.setSql(sql);
        return queryOrderBy;
    }
}
