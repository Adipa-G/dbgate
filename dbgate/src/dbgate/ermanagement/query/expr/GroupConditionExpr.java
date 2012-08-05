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
public class GroupConditionExpr extends BaseExpr
{
    public static GroupConditionExpr build()
    {
        return new GroupConditionExpr();
    }

    @Override
    public GroupConditionExpr field(Class type, String field)
    {
        return (GroupConditionExpr)super.field(type, field);
    }

    @Override
    public GroupConditionExpr field(Class type, String typeAlias, String field)
    {
        return (GroupConditionExpr)super.field(type, typeAlias, field,null);
    }

    @Override
    public GroupConditionExpr value(DBColumnType type, Object value)
    {
        return (GroupConditionExpr)super.value(type, value);
    }

    @Override
    public GroupConditionExpr values(DBColumnType type, Object... values)
    {
        return (GroupConditionExpr)super.values(type,values);
    }

    @Override
    public GroupConditionExpr sum()
    {
        return (GroupConditionExpr)super.sum();
    }

    @Override
    public GroupConditionExpr count()
    {
        return (GroupConditionExpr)super.count();
    }

    @Override
    public GroupConditionExpr custFunc(String func)
    {
        return (GroupConditionExpr)super.custFunc(func);
    }

    @Override
    public GroupConditionExpr eq()
    {
        return (GroupConditionExpr)super.eq();
    }

    @Override
    public GroupConditionExpr ge()
    {
        return (GroupConditionExpr)super.ge();
    }

    @Override
    public GroupConditionExpr gt()
    {
        return (GroupConditionExpr)super.gt();
    }

    @Override
    public GroupConditionExpr le()
    {
        return (GroupConditionExpr)super.le();
    }

    @Override
    public GroupConditionExpr lt()
    {
        return (GroupConditionExpr)super.lt();
    }

    @Override
    public GroupConditionExpr neq()
    {
        return (GroupConditionExpr)super.neq();
    }

    @Override
    public GroupConditionExpr between()
    {
        return (GroupConditionExpr)super.between();
    }

    @Override
    public GroupConditionExpr in()
    {
        return (GroupConditionExpr)super.in();
    }

    @Override
    public GroupConditionExpr and()
    {
        return (GroupConditionExpr)super.and();
    }

    @Override
    public GroupConditionExpr or()
    {
        return (GroupConditionExpr)super.or();
    }

    public GroupConditionExpr and(GroupConditionExpr... expressions)
    {
        return (GroupConditionExpr)super.and(expressions);
    }

    public GroupConditionExpr or(GroupConditionExpr... expressions)
    {
        return (GroupConditionExpr)super.or(expressions);
    }
}
