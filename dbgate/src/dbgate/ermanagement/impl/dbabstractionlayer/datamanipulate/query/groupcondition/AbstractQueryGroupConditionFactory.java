package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.groupcondition;

import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition.AbstractSqlQueryCondition;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition.IAbstractQueryCondition;
import dbgate.ermanagement.query.QueryConditionExpressionType;
import dbgate.ermanagement.query.QueryGroupConditionExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractQueryGroupConditionFactory
{
    public IAbstractQueryGroupCondition createGroupCondition(QueryGroupConditionExpressionType groupConditionExpressionType)
    {
        switch (groupConditionExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryGroupCondition();
            default:
                return null;
        }
    }
}
