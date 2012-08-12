package dbgate.ermanagement.query;

import dbgate.IQuerySelection;
import dbgate.ISelectionQuery;
import dbgate.exceptions.ExpressionParsingException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractExpressionSelection;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractSelectionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractSqlQuerySelection;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractTypeSelection;
import dbgate.ermanagement.query.expr.SelectExpr;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuerySelection
{
    private static AbstractSelectionFactory factory;

    public static void setFactory(AbstractSelectionFactory f)
    {
        factory = f;
    }

    public static IQuerySelection rawSql(String sql)
    {
        AbstractSqlQuerySelection querySelection = (AbstractSqlQuerySelection) factory.createSelection(
                QuerySelectionExpressionType.RAW_SQL);
        querySelection.setSql(sql);
        return querySelection;
    }

    public static IQuerySelection type(Class type)
    {
        AbstractTypeSelection selection = (AbstractTypeSelection) factory.createSelection(
                QuerySelectionExpressionType.TYPE);
        selection.setType(type);
        return selection;
    }

    public static IQuerySelection query(ISelectionQuery query,String alias) throws ExpressionParsingException
    {
        AbstractExpressionSelection expressionSelection = (AbstractExpressionSelection) factory.createSelection(QuerySelectionExpressionType.EXPRESSION);
        expressionSelection.setExpr(SelectExpr.build().query(query,alias));
        return expressionSelection;
    }

    private static IQuerySelection expression(SelectExpr expr)
    {
        AbstractExpressionSelection expressionSelection = (AbstractExpressionSelection) factory.createSelection(QuerySelectionExpressionType.EXPRESSION);
        expressionSelection.setExpr(expr);
        return expressionSelection;
    }

    public static IQuerySelection field(String field) throws ExpressionParsingException
    {
        return expression(SelectExpr.build().field(field));
    }

    public static IQuerySelection field(String field, String alias) throws ExpressionParsingException
    {
        return expression(SelectExpr.build().field(field,alias));
    }

    public static IQuerySelection field(Class type, String field, String alias) throws ExpressionParsingException
    {
        return expression(SelectExpr.build().field(type,field,alias));
    }

    public static IQuerySelection sum(Class type,String field,String alias) throws ExpressionParsingException
    {
        return expression(SelectExpr.build().field(type,field,alias).sum());
    }

    public static IQuerySelection count(Class type,String field,String alias) throws ExpressionParsingException
    {
        return expression(SelectExpr.build().field(type,field,alias).count());
    }

    public static IQuerySelection custFunction(String sqlFunction,Class type,String field,String alias) throws ExpressionParsingException
    {
        return expression(SelectExpr.build().field(type,field,alias).custFunc(sqlFunction));
    }
}
