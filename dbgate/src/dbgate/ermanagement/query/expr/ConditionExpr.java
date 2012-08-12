package dbgate.ermanagement.query.expr;

import dbgate.ColumnType;
import dbgate.ISelectionQuery;
import dbgate.ermanagement.exceptions.ExpressionParsingException;

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
    public ConditionExpr field(Class type, String field) throws ExpressionParsingException
    {
        return (ConditionExpr)super.field(type, field);
    }

    @Override
    public ConditionExpr field(Class type, String typeAlias, String field) throws ExpressionParsingException
    {
        return (ConditionExpr)super.field(type, typeAlias, field,null);
    }

    @Override
    public ConditionExpr value(ColumnType type, Object value) throws ExpressionParsingException
    {
        return (ConditionExpr)super.value(type, value);
    }

    @Override
    public ConditionExpr values(ColumnType type, Object... values) throws ExpressionParsingException
    {
        return (ConditionExpr)super.value(type, values);
    }

    @Override
    public ConditionExpr query(ISelectionQuery query) throws ExpressionParsingException
    {
        return (ConditionExpr)super.query(query);
    }

    @Override
    public ConditionExpr eq() throws ExpressionParsingException
    {
        return (ConditionExpr)super.eq();
    }

    @Override
    public ConditionExpr ge() throws ExpressionParsingException
    {
        return (ConditionExpr)super.ge();
    }

    @Override
    public ConditionExpr gt() throws ExpressionParsingException
    {
        return (ConditionExpr)super.gt();
    }

    @Override
    public ConditionExpr le() throws ExpressionParsingException
    {
        return (ConditionExpr)super.le();
    }

    @Override
    public ConditionExpr lt() throws ExpressionParsingException
    {
        return (ConditionExpr)super.lt();
    }

    @Override
    public ConditionExpr neq() throws ExpressionParsingException
    {
        return (ConditionExpr)super.neq();
    }

    @Override
    public ConditionExpr like() throws ExpressionParsingException
    {
        return (ConditionExpr)super.like();
    }

    @Override
    public ConditionExpr between() throws ExpressionParsingException
    {
        return (ConditionExpr)super.between();
    }

    @Override
    public ConditionExpr in() throws ExpressionParsingException
    {
        return (ConditionExpr)super.in();
    }

    @Override
    public ConditionExpr exists() throws ExpressionParsingException
    {
        return (ConditionExpr)super.exists();
    }

    @Override
    public ConditionExpr notExists() throws ExpressionParsingException
    {
        return (ConditionExpr)super.notExists();
    }

    @Override
    public ConditionExpr and() throws ExpressionParsingException
    {
        return (ConditionExpr)super.and();
    }

    @Override
    public ConditionExpr or() throws ExpressionParsingException
    {
        return (ConditionExpr)super.or();
    }

    public ConditionExpr and(ConditionExpr... expressions) throws ExpressionParsingException
    {
        return (ConditionExpr)super.and(expressions);
    }

    public ConditionExpr or(ConditionExpr... expressions) throws ExpressionParsingException
    {
        return (ConditionExpr)super.or(expressions);
    }
}
