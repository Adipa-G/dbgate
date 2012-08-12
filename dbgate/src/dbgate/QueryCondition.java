package dbgate;

import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.condition.AbstractConditionFactory;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.condition.AbstractExpressionCondition;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.condition.AbstractSqlQueryCondition;
import dbgate.ermanagement.query.IQueryCondition;
import dbgate.ermanagement.query.QueryConditionExpressionType;
import dbgate.ermanagement.query.expr.ConditionExpr;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/26/12
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryCondition
{
    private static AbstractConditionFactory factory;

    public static void setFactory(AbstractConditionFactory f)
    {
        factory = f;
    }

    public static IQueryCondition rawSql(String sql)
    {
        AbstractSqlQueryCondition queryCondition = (AbstractSqlQueryCondition) factory.createCondition(
                QueryConditionExpressionType.RAW_SQL);
        queryCondition.setSql(sql);
        return queryCondition;
    }

    public static IQueryCondition expression(ConditionExpr expr)
    {
        AbstractExpressionCondition queryCondition = (AbstractExpressionCondition) factory.createCondition(
                QueryConditionExpressionType.EXPRESSION);
        queryCondition.setExpr(expr);
        return queryCondition;
    }
}
