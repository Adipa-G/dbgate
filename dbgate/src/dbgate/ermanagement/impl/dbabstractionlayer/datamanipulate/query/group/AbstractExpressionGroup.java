package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group;

import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.AbstractExpressionProcessor;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryGroupExpressionType;
import dbgate.ermanagement.query.expr.GroupExpr;
import dbgate.ermanagement.query.expr.segments.FieldSegment;
import dbgate.ermanagement.query.expr.segments.ISegment;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractExpressionGroup implements IAbstractGroup
{
    private GroupExpr expr;
    private AbstractExpressionProcessor processor;

    public AbstractExpressionGroup()
    {
        processor = new AbstractExpressionProcessor();
    }

    public GroupExpr getExpr()
    {
        return expr;
    }

    public void setExpr(GroupExpr expr)
    {
        this.expr = expr;
    }

    @Override
    public QueryGroupExpressionType getGroupExpressionType()
    {
        return QueryGroupExpressionType.EXPRESSION;
    }

    @Override
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo)
    {
        ISegment rootSegment = expr.getRootSegment();
        switch (rootSegment.getSegmentType())
        {
            case FIELD:
                return processor.getFieldName((FieldSegment) rootSegment, false, buildInfo);
        }
        return null;
    }
}
