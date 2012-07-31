package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryFrom;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.AbstractQueryFromFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.AbstractSqlQueryFrom;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryFrom
{
    private static AbstractQueryFromFactory factory;

    public static void setFactory(AbstractQueryFromFactory f)
    {
        factory = f;
    }

    public static IQueryFrom RawSql(String sql)
    {
        AbstractSqlQueryFrom queryFrom = (AbstractSqlQueryFrom) factory.createFrom(QueryFromType.RAW_SQL);
        queryFrom.setSql(sql);
        return queryFrom;
    }
}
