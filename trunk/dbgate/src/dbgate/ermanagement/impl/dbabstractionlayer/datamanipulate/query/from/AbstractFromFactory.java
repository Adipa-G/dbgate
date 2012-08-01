package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.ermanagement.query.QueryFromExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractFromFactory
{
    public IAbstractFrom createFrom(QueryFromExpressionType fromExpressionType)
    {
        switch (fromExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryFrom();
            case TYPE:
                return new AbstractTypeFrom();
            case QUERY:
                return new AbstractSubQueryFrom();
            case QUERY_UNION:
                return new AbstractUnionFrom();
            default:
                return null;
        }
    }
}
