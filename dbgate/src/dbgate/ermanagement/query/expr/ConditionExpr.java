package dbgate.ermanagement.query.expr;

import dbgate.DBColumnType;
import dbgate.ermanagement.ISelectionQuery;

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
    public ConditionExpr field(Class type, String typeAlias, String field)
    {
        return (ConditionExpr)super.field(type, typeAlias, field,null);
    }

    @Override
    public ConditionExpr value(DBColumnType type, Object value)
    {
        return (ConditionExpr)super.value(type, value);
    }

    @Override
    public ConditionExpr values(DBColumnType type, Object... values)
    {
        return (ConditionExpr)super.value(type, values);
    }

    @Override
    public ConditionExpr query(ISelectionQuery query)
    {
        return (ConditionExpr)super.query(query);
    }

    @Override
    public ConditionExpr eq()
    {
        return (ConditionExpr)super.eq();
    }

    @Override
    public ConditionExpr ge()
    {
        return (ConditionExpr)super.ge();
    }

    @Override
    public ConditionExpr gt()
    {
        return (ConditionExpr)super.gt();
    }

    @Override
    public ConditionExpr le()
    {
        return (ConditionExpr)super.le();
    }

    @Override
    public ConditionExpr lt()
    {
        return (ConditionExpr)super.lt();
    }

    @Override
    public ConditionExpr neq()
    {
        return (ConditionExpr)super.neq();
    }

    @Override
    public ConditionExpr like()
    {
        return (ConditionExpr)super.like();
    }

    @Override
    public ConditionExpr between()
    {
        return (ConditionExpr)super.between();
    }

    @Override
    public ConditionExpr in()
    {
        return (ConditionExpr)super.in();
    }

    @Override
    public ConditionExpr exists()
    {
        return (ConditionExpr)super.exists();
    }

    @Override
    public ConditionExpr notExists()
    {
        return (ConditionExpr)super.notExists();
    }

    @Override
    public ConditionExpr and()
    {
        return (ConditionExpr)super.and();
    }

    @Override
    public ConditionExpr or()
    {
        return (ConditionExpr)super.or();
    }

    public ConditionExpr and(ConditionExpr... expressions)
    {
        return (ConditionExpr)super.and(expressions);
    }

    public ConditionExpr or(ConditionExpr... expressions)
    {
        return (ConditionExpr)super.or(expressions);
    }
}
