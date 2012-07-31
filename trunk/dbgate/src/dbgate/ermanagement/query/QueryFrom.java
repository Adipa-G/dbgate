package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryFrom;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.AbstractQueryFromFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.AbstractSqlQueryFrom;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.AbstractTypeQueryFrom;

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

    public static IQueryFrom rawSql(String sql)
    {
        AbstractSqlQueryFrom queryFrom = (AbstractSqlQueryFrom) factory.createFrom(QueryFromExpressionType.RAW_SQL);
        queryFrom.setSql(sql);
        return queryFrom;
    }

    public static IQueryFrom type(Class type)
    {
        AbstractTypeQueryFrom queryFrom = (AbstractTypeQueryFrom) factory.createFrom(QueryFromExpressionType.TYPE);
        queryFrom.setType(type);
        return queryFrom;
    }
}
