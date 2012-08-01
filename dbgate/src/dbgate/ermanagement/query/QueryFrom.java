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
    private static AbstractFromFactory factory;

    public static void setFactory(AbstractFromFactory f)
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
        AbstractTypeFrom from = (AbstractTypeFrom) factory.createFrom(QueryFromExpressionType.TYPE);
        from.setType(type);
        if (alias != null && alias.length() > 0)
        {
            from.setAlias(alias);
        }
        return from;
    }

    public static IQueryFrom query(ISelectionQuery query)
    {
        return query(query, null);
    }

    public static IQueryFrom query(ISelectionQuery query,String alias)
    {
        AbstractSubQueryFrom queryFromSub = (AbstractSubQueryFrom) factory.createFrom(QueryFromExpressionType.QUERY);
        queryFromSub.setQuery(query);
        if (alias != null && alias.length() > 0)
        {
            queryFromSub.setAlias(alias);
        }
        return queryFromSub;
    }

    public static IQueryFrom queryUnion(boolean all,ISelectionQuery... queries)
    {
        AbstractUnionFrom from = (AbstractUnionFrom) factory.createFrom(QueryFromExpressionType.QUERY_UNION);
        from.setQueries(queries);
        from.setAll(all);
        return from;
    }
}
