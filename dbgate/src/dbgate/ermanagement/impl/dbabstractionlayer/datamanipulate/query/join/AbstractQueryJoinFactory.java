package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join;

import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby.AbstractSqlQueryOrderBy;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby.IAbstractQueryOrderBy;
import dbgate.ermanagement.query.QueryJoinExpressionType;
import dbgate.ermanagement.query.QueryOrderByExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractQueryJoinFactory
{
    public IAbstractQueryJoin createOrderBy(QueryJoinExpressionType joinExpressionType)
    {
        switch (joinExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryJoin();
            default:
                return null;
        }
    }
}
