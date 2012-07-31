package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryFrom;
import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.*;

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
        return type(type,null);
    }

    public static IQueryFrom type(Class type,String alias)
    {
        AbstractTypeQueryFrom queryFrom = (AbstractTypeQueryFrom) factory.createFrom(QueryFromExpressionType.TYPE);
        queryFrom.setType(type);
        if (alias != null && alias.length() > 0)
        {
            queryFrom.setAlias(alias);
        }
        return queryFrom;
    }

    public static IQueryFrom query(ISelectionQuery query)
    {
        return query(query, null);
    }

    public static IQueryFrom query(ISelectionQuery query,String alias)
    {
        AbstractQueryQueryFrom queryFrom = (AbstractQueryQueryFrom) factory.createFrom(QueryFromExpressionType.QUERY);
        queryFrom.setQuery(query);
        if (alias != null && alias.length() > 0)
        {
            queryFrom.setAlias(alias);
        }
        return queryFrom;
    }

    public static IQueryFrom queryUnion(boolean all,ISelectionQuery... queries)
    {
        AbstractQueryUnionQueryFrom queryFrom = (AbstractQueryUnionQueryFrom) factory.createFrom(QueryFromExpressionType.QUERY_UNION);
        queryFrom.setQueries(queries);
        queryFrom.setAll(all);
        return queryFrom;
    }
}
