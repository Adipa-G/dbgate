package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.groupcondition;

import dbgate.exceptions.ExpressionParsingException;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.AbstractExpressionProcessor;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryGroupConditionExpressionType;
import dbgate.ermanagement.query.expr.GroupConditionExpr;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractExpressionGroupCondition implements IAbstractGroupCondition
{
    private GroupConditionExpr expr;
    private AbstractExpressionProcessor processor;

    public AbstractExpressionGroupCondition()
    {
        processor = new AbstractExpressionProcessor();
    }

    public GroupConditionExpr getExpr()
    {
        return expr;
    }

    public void setExpr(GroupConditionExpr expr)
    {
        this.expr = expr;
    }

    @Override
    public QueryGroupConditionExpressionType getGroupConditionExpressionType()
    {
        return QueryGroupConditionExpressionType.EXPRESSION;
    }

    @Override
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        return processor.process(null,expr.getRootSegment(),buildInfo,dbLayer);
    }
}
