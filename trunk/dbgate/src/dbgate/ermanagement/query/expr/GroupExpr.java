package dbgate.ermanagement.query.expr;

import dbgate.ermanagement.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroupExpr extends BaseExpr
{
    public static GroupExpr build()
    {
        return new GroupExpr();
    }

    @Override
    public GroupExpr field(Class type, String field) throws ExpressionParsingException
    {
        return (GroupExpr)super.field(type, field);
    }
}
