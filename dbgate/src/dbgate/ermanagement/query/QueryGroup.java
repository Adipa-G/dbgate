package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryGroup;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.AbstractQueryGroupFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.AbstractSqlQueryGroup;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryGroup
{
    private static AbstractQueryGroupFactory factory;

    public static void setFactory(AbstractQueryGroupFactory f)
    {
        factory = f;
    }

    public static IQueryGroup RawSql(String sql)
    {
        AbstractSqlQueryGroup queryGroup = (AbstractSqlQueryGroup) factory.createGroup(QueryGroupExpressionType.RAW_SQL);
        queryGroup.setSql(sql);
        return queryGroup;
    }
}
