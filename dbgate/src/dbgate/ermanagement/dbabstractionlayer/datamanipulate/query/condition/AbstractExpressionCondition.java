package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.condition;

import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.AbstractExpressionProcessor;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryConditionExpressionType;
import dbgate.ermanagement.query.expr.ConditionExpr;
import dbgate.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractExpressionCondition implements IAbstractCondition
{
    private ConditionExpr expr;
    private AbstractExpressionProcessor processor;

    public AbstractExpressionCondition()
    {
        processor = new AbstractExpressionProcessor();
    }

    public ConditionExpr getExpr()
    {
        return expr;
    }

    public void setExpr(ConditionExpr expr)
    {
        this.expr = expr;
    }

    @Override
    public QueryConditionExpressionType getConditionExpressionType()
    {
        return QueryConditionExpressionType.EXPRESSION;
    }

    @Override
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        return processor.process(null,expr.getRootSegment(),buildInfo,dbLayer);
    }
}
