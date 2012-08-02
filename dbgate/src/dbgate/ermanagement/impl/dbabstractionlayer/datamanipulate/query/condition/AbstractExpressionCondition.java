package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.AbstractExpressionProcessor;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.IAbstractSelection;
import dbgate.ermanagement.query.QueryConditionExpressionType;
import dbgate.ermanagement.query.QuerySelectionExpressionType;
import dbgate.ermanagement.query.expr.ConditionExpr;
import dbgate.ermanagement.query.expr.SelectExpr;
import dbgate.ermanagement.query.expr.segments.FieldSegment;
import dbgate.ermanagement.query.expr.segments.GroupFunctionSegment;
import dbgate.ermanagement.query.expr.segments.ISegment;
import dbgate.ermanagement.query.expr.segments.SegmentType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;

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
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo)
    {
        return processor.process(null,expr.getRootSegment(),buildInfo);
    }
}
