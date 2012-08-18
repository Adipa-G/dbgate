package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.exceptions.ExpressionParsingException;
import dbgate.exceptions.RetrievalException;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.AbstractExpressionProcessor;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QuerySelectionExpressionType;
import dbgate.ermanagement.query.expr.SelectExpr;
import dbgate.ermanagement.query.expr.segments.FieldSegment;
import dbgate.ermanagement.query.expr.segments.GroupFunctionSegment;
import dbgate.ermanagement.query.expr.segments.ISegment;
import dbgate.ermanagement.query.expr.segments.QuerySegment;

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
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        ISegment rootSegment = expr.getRootSegment();
        switch (rootSegment.getSegmentType())
        {
            case GROUP:
                return processor.getGroupFunction((GroupFunctionSegment) rootSegment, true, buildInfo);
            case FIELD:
                return processor.getFieldName((FieldSegment) rootSegment, true, buildInfo);
            case QUERY:
                QuerySegment querySegment = (QuerySegment) rootSegment;
                buildInfo = dbLayer.getDataManipulate().processQuery(buildInfo,querySegment.getQuery().getStructure());
                return "(" + buildInfo.getExecInfo().getSql() + ") as " + querySegment.getAlias();
        }
        return null;
    }

    @Override
    public Object retrieve(ResultSet rs, Connection con, QueryBuildInfo buildInfo) throws RetrievalException
    {
        try
        {
            String column = null;
            ISegment rootSegment = expr.getRootSegment();

            FieldSegment fieldSegment = null;
            switch (rootSegment.getSegmentType())
            {
                case GROUP:
                    fieldSegment = ((GroupFunctionSegment)rootSegment).getSegmentToGroup();
                    //no break here as processing continues to field segment
                case FIELD:
                    if (fieldSegment == null)
                    {
                        fieldSegment = (FieldSegment) rootSegment;
                    }
                    String alias = fieldSegment.getAlias();
                    column = alias != null && alias.length() > 0? alias : processor.getColumn(fieldSegment,buildInfo).getColumnName();
                    break;
                case QUERY:
                    QuerySegment querySegment = (QuerySegment) rootSegment;
                    column = querySegment.getAlias();
                    break;
            }

            Object obj = rs.getObject(column);
            return obj;
        }
        catch (Exception ex)
        {
            throw new RetrievalException(ex.getMessage(),ex);
        }
    }
}
