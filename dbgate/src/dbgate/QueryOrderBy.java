package dbgate;

import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.orderby.AbstractExpressionOrderBy;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.orderby.AbstractOrderByFactory;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.orderby.AbstractSqlQueryOrderBy;
import dbgate.ermanagement.query.IQueryOrderBy;
import dbgate.ermanagement.query.QueryOrderByExpressionType;
import dbgate.ermanagement.query.expr.OrderByExpr;
import dbgate.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryOrderBy
{
    private static AbstractOrderByFactory factory;

    public static void setFactory(AbstractOrderByFactory f)
    {
        factory = f;
    }

    public static IQueryOrderBy rawSql(String sql)
    {
        AbstractSqlQueryOrderBy queryOrderBy = (AbstractSqlQueryOrderBy) factory.createOrderBy(
                QueryOrderByExpressionType.RAW_SQL);
        queryOrderBy.setSql(sql);
        return queryOrderBy;
    }

    public static IQueryOrderBy field(Class type,String field) throws ExpressionParsingException
    {
        return field(type,field,QueryOrderType.ASCEND);
    }

    public static IQueryOrderBy field(Class type,String field,QueryOrderType orderType) throws
    ExpressionParsingException
    {
        AbstractExpressionOrderBy expressionOrderBy = (AbstractExpressionOrderBy) factory.createOrderBy(QueryOrderByExpressionType.EXPRESSION);
        expressionOrderBy.setExpr(OrderByExpr.build().field(type, field));
        expressionOrderBy.setOrderType(orderType);
        return expressionOrderBy;
    }
}
