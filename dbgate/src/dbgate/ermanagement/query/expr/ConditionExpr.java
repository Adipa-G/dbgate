package dbgate.ermanagement.query.expr;

import dbgate.DBColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConditionExpr extends BaseExpr
{
    public static ConditionExpr build()
    {
        return new ConditionExpr();
    }

    @Override
    public ConditionExpr field(Class type, String field)
    {
        return (ConditionExpr)super.field(type, field);
    }

    @Override
    public ConditionExpr value(DBColumnType type, Object value)
    {
        return (ConditionExpr)super.value(type, value);
    }

    @Override
    public ConditionExpr eq()
    {
        return (ConditionExpr)super.eq();
    }
}
