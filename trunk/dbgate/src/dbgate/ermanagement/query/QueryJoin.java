package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryJoin;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join.AbstractJoinFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join.AbstractSqlQueryJoin;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryJoin
{
    private static AbstractJoinFactory factory;

    public static void setFactory(AbstractJoinFactory f)
    {
        factory = f;
    }

    public static IQueryJoin rawSql(String sql)
    {
        AbstractSqlQueryJoin queryJoin = (AbstractSqlQueryJoin) factory.createOrderBy(QueryJoinExpressionType.RAW_SQL);
        queryJoin.setSql(sql);
        return queryJoin;
    }
}
