package dbgate.ermanagement.query.expr;

import dbgate.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderByExpr extends BaseExpr
{
    public static OrderByExpr build()
    {
        return new OrderByExpr();
    }

    @Override
    public OrderByExpr field(Class type, String field) throws ExpressionParsingException
    {
        return (OrderByExpr)super.field(type, field);
    }
}
