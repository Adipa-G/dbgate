package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby;

import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.AbstractSqlQueryGroup;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.IAbstractQueryGroup;
import dbgate.ermanagement.query.QueryGroupExpressionType;
import dbgate.ermanagement.query.QueryOrderByExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractQueryOrderByFactory
{
    public IAbstractQueryOrderBy createOrderBy(QueryOrderByExpressionType orderByExpressionType)
    {
        switch (orderByExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryOrderBy();
            default:
                return null;
        }
    }
}
