package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby;

import dbgate.QueryOrderType;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.AbstractExpressionProcessor;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryOrderByExpressionType;
import dbgate.ermanagement.query.expr.OrderByExpr;
import dbgate.ermanagement.query.expr.segments.FieldSegment;
import dbgate.ermanagement.query.expr.segments.ISegment;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractExpressionOrderBy implements IAbstractOrderBy
{
    private OrderByExpr expr;
    private QueryOrderType orderType;
    private AbstractExpressionProcessor processor;

    public AbstractExpressionOrderBy()
    {
        processor = new AbstractExpressionProcessor();
    }

    public QueryOrderType getOrderType()
    {
        return orderType;
    }

    public void setOrderType(QueryOrderType orderType)
    {
        this.orderType = orderType;
    }

    public OrderByExpr getExpr()
    {
        return expr;
    }

    public void setExpr(OrderByExpr expr)
    {
        this.expr = expr;
    }

    @Override
    public QueryOrderByExpressionType getOrderByExpressionType()
    {
        return QueryOrderByExpressionType.EXPRESSION;
    }

    @Override
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo)
    {
        ISegment rootSegment = expr.getRootSegment();
        switch (rootSegment.getSegmentType())
        {
            case FIELD:
                String sql = processor.getFieldName((FieldSegment) rootSegment, false, buildInfo);
                switch (orderType)
                {
                    case ASCEND:
                        sql += " ASC"; break;
                    case DESCEND:
                        sql += " DESC"; break;
                }
                return sql;
        }
        return null;
    }
}
