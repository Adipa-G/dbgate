package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.ermanagement.query.QueryFromExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractQueryFromFactory
{
    public IAbstractQueryFrom createFrom(QueryFromExpressionType fromExpressionType)
    {
        switch (fromExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryFrom();
            case TYPE:
                return new AbstractTypeQueryFrom();
            case QUERY:
                return new AbstractQueryQueryFrom();
            case QUERY_UNION:
                return new AbstractQueryUnionQueryFrom();
            default:
                return null;
        }
    }
}
