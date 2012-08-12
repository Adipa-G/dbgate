package dbgate.ermanagement.query;

import dbgate.IQueryGroupCondition;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.groupcondition.AbstractExpressionGroupCondition;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.groupcondition.AbstractGroupConditionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.groupcondition.AbstractSqlQueryGroupCondition;
import dbgate.ermanagement.query.expr.GroupConditionExpr;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryGroupCondition
{
    private static AbstractGroupConditionFactory factory;

    public static void setFactory(AbstractGroupConditionFactory f)
    {
        factory = f;
    }

    public static IQueryGroupCondition rawSql(String sql)
    {
        AbstractSqlQueryGroupCondition sqlQueryGroupCondition = (AbstractSqlQueryGroupCondition) factory.createGroupCondition(QueryGroupConditionExpressionType.RAW_SQL);
        sqlQueryGroupCondition.setSql(sql);
        return sqlQueryGroupCondition;
    }

    public static IQueryGroupCondition expression(GroupConditionExpr expr)
    {
        AbstractExpressionGroupCondition expressionGroupCondition = (AbstractExpressionGroupCondition) factory.createGroupCondition(QueryGroupConditionExpressionType.EXPRESSION);
        expressionGroupCondition.setExpr(expr);
        return expressionGroupCondition;
    }
}
