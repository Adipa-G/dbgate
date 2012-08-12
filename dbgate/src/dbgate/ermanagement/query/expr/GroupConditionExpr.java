package dbgate.ermanagement.query.expr;

import dbgate.ColumnType;
import dbgate.ermanagement.exceptions.ExpressionParsingException;

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
    public GroupConditionExpr field(Class type, String field) throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.field(type, field);
    }

    @Override
    public GroupConditionExpr field(Class type, String typeAlias, String field) throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.field(type, typeAlias, field,null);
    }

    @Override
    public GroupConditionExpr value(ColumnType type, Object value) throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.value(type, value);
    }

    @Override
    public GroupConditionExpr values(ColumnType type, Object... values) throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.values(type,values);
    }

    @Override
    public GroupConditionExpr sum() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.sum();
    }

    @Override
    public GroupConditionExpr count() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.count();
    }

    @Override
    public GroupConditionExpr custFunc(String func) throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.custFunc(func);
    }

    @Override
    public GroupConditionExpr eq() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.eq();
    }

    @Override
    public GroupConditionExpr ge() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.ge();
    }

    @Override
    public GroupConditionExpr gt() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.gt();
    }

    @Override
    public GroupConditionExpr le() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.le();
    }

    @Override
    public GroupConditionExpr lt() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.lt();
    }

    @Override
    public GroupConditionExpr neq() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.neq();
    }

    @Override
    public GroupConditionExpr between() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.between();
    }

    @Override
    public GroupConditionExpr in() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.in();
    }

    @Override
    public GroupConditionExpr and() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.and();
    }

    @Override
    public GroupConditionExpr or() throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.or();
    }

    public GroupConditionExpr and(GroupConditionExpr... expressions) throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.and(expressions);
    }

    public GroupConditionExpr or(GroupConditionExpr... expressions) throws ExpressionParsingException
    {
        return (GroupConditionExpr)super.or(expressions);
    }
}
