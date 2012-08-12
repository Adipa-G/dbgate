package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.join;

import dbgate.ermanagement.query.QueryJoinExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractJoinFactory
{
    public IAbstractJoin createOrderBy(QueryJoinExpressionType joinExpressionType)
    {
        switch (joinExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryJoin();
            case TYPE:
                return new AbstractTypeJoin();
            default:
                return null;
        }
    }
}
