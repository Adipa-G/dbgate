package dbgate.ermanagement.query.expr;

import dbgate.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class JoinExpr extends BaseExpr
{
    public static JoinExpr build()
    {
        return new JoinExpr();
    }

    @Override
    public JoinExpr field(Class type, String field) throws ExpressionParsingException
    {
        return (JoinExpr)super.field(type, field);
    }

    @Override
    public JoinExpr field(Class type, String typeAlias, String field) throws ExpressionParsingException
    {
        return (JoinExpr)super.field(type, typeAlias, field,null);
    }

    @Override
    public JoinExpr eq() throws ExpressionParsingException
    {
        return (JoinExpr)super.eq();
    }

    @Override
    public JoinExpr and() throws ExpressionParsingException
    {
        return (JoinExpr)super.and();
    }

    @Override
    public JoinExpr or() throws ExpressionParsingException
    {
        return (JoinExpr)super.or();
    }

    public JoinExpr and(JoinExpr... expressions) throws ExpressionParsingException
    {
        return (JoinExpr)super.and(expressions);
    }

    public JoinExpr or(JoinExpr... expressions) throws ExpressionParsingException
    {
        return (JoinExpr)super.or(expressions);
    }
}
