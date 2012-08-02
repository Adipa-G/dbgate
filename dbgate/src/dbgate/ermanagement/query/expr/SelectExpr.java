package dbgate.ermanagement.query.expr;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectExpr extends BaseExpr
{
    @Override
    public SelectExpr sum()
    {
        return (SelectExpr)super.sum();
    }

    @Override
    public SelectExpr count()
    {
        return (SelectExpr)super.count();
    }

    @Override
    public SelectExpr custFunc(String func)
    {
        return (SelectExpr)super.custFunc(func);
    }

    @Override
    public SelectExpr field(Class type, String field)
    {
        return (SelectExpr)super.field(type, field);
    }

    @Override
    public SelectExpr field(Class type, String field, String alias)
    {
        return (SelectExpr)super.field(type, field,alias);
    }

    public static SelectExpr build()
    {
        return new SelectExpr();
    }
}
