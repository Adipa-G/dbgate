package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.AbstractExpressionProcessor;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QuerySelectionExpressionType;
import dbgate.ermanagement.query.expr.SelectExpr;
import dbgate.ermanagement.query.expr.segments.*;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractExpressionSelection implements IAbstractSelection
{
    private SelectExpr expr;
    private AbstractExpressionProcessor processor;
    
    public AbstractExpressionSelection()
    {
        processor = new AbstractExpressionProcessor();
    }

    public SelectExpr getExpr()
    {
        return expr;
    }

    public void setExpr(SelectExpr expr)
    {
        this.expr = expr;
    }

    @Override
    public QuerySelectionExpressionType getSelectionType()
    {
        return QuerySelectionExpressionType.EXPRESSION;
    }

    @Override
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo)
    {
        ISegment rootSegment = expr.getRootSegment();
        if (rootSegment.getSegmentType() == SegmentType.GROUP)
        {
            return processor.getGroupFunction((GroupFunctionSegment) rootSegment, true, buildInfo);
        }
        else
        {
            return processor.getFieldName((FieldSegment) rootSegment, true, buildInfo);
        }
    }

    @Override
    public Object retrieve(ResultSet rs, Connection con, QueryBuildInfo buildInfo) throws RetrievalException
    {
        try
        {
            FieldSegment fieldSegment = null;
            ISegment rootSegment = expr.getRootSegment();
            if (rootSegment.getSegmentType() == SegmentType.GROUP)
            {
                fieldSegment = ((GroupFunctionSegment)rootSegment).getSegmentToGroup();
            }
            else
            {
                fieldSegment = (FieldSegment)rootSegment;
            }

            String alias = fieldSegment.getAlias();
            String columnName = alias != null && alias.length() > 0? alias : processor.getColumn(fieldSegment).getColumnName();
            Object obj = rs.getObject(columnName);
            return obj;
        }
        catch (Exception ex)
        {
            throw new RetrievalException(ex.getMessage(),ex);
        }
    }
}
