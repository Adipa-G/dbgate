package dbgate.ermanagement.query.expr;

import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectExpr extends BaseExpr
{
    public static SelectExpr build()
    {
        return new SelectExpr();
    }

    @Override
    public SelectExpr field(String field) throws ExpressionParsingException
    {
        return (SelectExpr)super.field(field);
    }

    @Override
    public SelectExpr field(String field, String alias) throws ExpressionParsingException
    {
        return (SelectExpr)super.field(field,alias);
    }

    @Override
    public SelectExpr field(Class type, String field) throws ExpressionParsingException
    {
        return (SelectExpr)super.field(type, field);
    }

    @Override
    public SelectExpr field(Class type, String field, String alias) throws ExpressionParsingException
    {
        return (SelectExpr)super.field(type, field,alias);
    }

    @Override
    public SelectExpr query(ISelectionQuery query, String alias) throws ExpressionParsingException
    {
        return (SelectExpr)super.query(query,alias);
    }

    @Override
    public SelectExpr sum() throws ExpressionParsingException
    {
        return (SelectExpr)super.sum();
    }

    @Override
    public SelectExpr count() throws ExpressionParsingException
    {
        return (SelectExpr)super.count();
    }

    @Override
    public SelectExpr custFunc(String func) throws ExpressionParsingException
    {
        return (SelectExpr)super.custFunc(func);
    }
}
