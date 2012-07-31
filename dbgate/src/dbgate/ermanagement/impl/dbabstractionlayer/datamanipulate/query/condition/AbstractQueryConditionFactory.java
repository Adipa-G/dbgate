package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition;

import dbgate.ermanagement.query.QueryConditionExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractQueryConditionFactory
{
    public IAbstractQueryCondition createCondition(QueryConditionExpressionType conditionExpressionType)
    {
        switch (conditionExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryCondition();
            default:
                return null;
        }
    }
}
