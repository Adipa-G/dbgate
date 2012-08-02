package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecParam;
import dbgate.ermanagement.query.expr.segments.*;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractExpressionProcessor
{
    private String appendAlias(String sql,FieldSegment fieldSegment)
    {
        if (fieldSegment.getAlias() != null && fieldSegment.getAlias().length() > 0)
        {
            return sql + " AS " + fieldSegment.getAlias() + " ";
        }
        return sql;
    }

    public IDBColumn getColumn(FieldSegment segment)
    {
        Collection<IDBColumn> columns = null;
        try
        {
            columns = CacheManager.fieldCache.getColumns(segment.getType());
        }
        catch (FieldCacheMissException e)
        {
            try
            {
                CacheManager.fieldCache.register(segment.getType(), (ServerRODBClass) segment.getType().newInstance());
                columns = CacheManager.fieldCache.getColumns(segment.getType());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if (columns != null)
        {
            for (IDBColumn column : columns)
            {
                if (column.getAttributeName().equals(segment.getField()))
                {
                    return column;
                }
            }
        }
        return null;
    }

    public String getFieldName(FieldSegment fieldSegment, boolean withAlias, QueryBuildInfo buildInfo)
    {
        String tableAlias = buildInfo.getAlias(fieldSegment.getType());
        tableAlias = (tableAlias == null)?"" : tableAlias + ".";
        IDBColumn column = getColumn(fieldSegment);

        if (column != null)
        {
            String sql = tableAlias + column.getColumnName();
            if (withAlias)
            {
                sql = appendAlias(sql,fieldSegment);
            }
            return sql;
        }
        else
        {
            return "<incorrect column for " + fieldSegment.getField() + ">";
        }
    }

    public String getGroupFunction(GroupFunctionSegment groupSegment, boolean withAlias, QueryBuildInfo buildInfo)
    {
        FieldSegment fieldSegment = (FieldSegment) groupSegment.getSegmentToGroup();
        String sql = getFieldName(fieldSegment, false, buildInfo);
        switch (groupSegment.getGroupFunctionMode())
        {
            case COUNT:
                sql = " COUNT(" + sql + ") ";
                break;
            case SUM:
                sql = " SUM(" + sql + ") ";
                break;
            case CUST_FUNC:
                sql = " " + groupSegment.getCustFunction() + "(" + sql + ") ";
                break;
        }
        if (withAlias)
        {
            sql = appendAlias(sql,fieldSegment);
        }
        return sql;
    }

    public String process(StringBuilder sb,ISegment segment,QueryBuildInfo buildInfo)
    {
        if (sb == null) sb = new StringBuilder();
        switch (segment.getSegmentType())
        {
            case FIELD:
                processField(sb,(FieldSegment) segment,buildInfo);
                break;
            case GROUP:
                processGroup(sb,(GroupFunctionSegment) segment,buildInfo);
                break;
            case VALUE:
                processValue(sb,(ValueSegment) segment,buildInfo);
                break;
            case COMPARE:
                processCompare(sb,(CompareSegment) segment,buildInfo);
                break;
            case MERGE:
                processMerge(sb,(MergeSegment)segment,buildInfo);
                break;
        }
        return sb.toString();
    }

    private void processField(StringBuilder sb,FieldSegment segment,QueryBuildInfo buildInfo)
    {
        String fieldName = getFieldName(segment,false,buildInfo);
        sb.append(fieldName);
    }

    private void processGroup(StringBuilder sb,GroupFunctionSegment segment,QueryBuildInfo buildInfo)
    {
        String groupFunction = getGroupFunction(segment, false, buildInfo);
        sb.append(groupFunction);
    }

    private void processValue(StringBuilder sb,ValueSegment segment,QueryBuildInfo buildInfo)
    {
        sb.append("?");
        
        QueryExecParam param = new QueryExecParam();
        param.setIndex(buildInfo.getExecInfo().getParams().size());
        param.setType(segment.getType());
        param.setValue(segment.getValue());
        buildInfo.getExecInfo().getParams().add(param);
    }
    
    private void processCompare(StringBuilder sb,CompareSegment segment,QueryBuildInfo buildInfo)
    {
        process(sb,segment.getLeft(),buildInfo);
        if (segment.getMode() == CompareSegmentMode.BETWEEN)
        {
            processBetween(sb,segment,buildInfo);
            return;
        }
        if (segment.getMode() == CompareSegmentMode.IN)
        {
            if (segment.getRight().getSegmentType() == SegmentType.VALUE)
            {
                processInValues(sb, segment, buildInfo);
            }
            return;
        }

        switch (segment.getMode())
        {
            case EQ:
                sb.append(" = ");break;
            case GE:
                sb.append(" >= ");break;
            case GT:
                sb.append(" > ");break;
            case LE:
                sb.append(" <= ");break;
            case LT:
                sb.append(" < ");break;
            case LIKE:
                sb.append(" like ");break;
            case NEQ:
                sb.append(" <> ");break;
            default:
                break;
        }
        process(sb,segment.getRight(),buildInfo);
    }

    private void processBetween(StringBuilder sb,CompareSegment segment,QueryBuildInfo buildInfo)
    {
        sb.append(" BETWEEN ? AND ? ");
        ValueSegment valueSegment = (ValueSegment) segment.getRight();
        Object[] values = (Object[]) valueSegment.getValue();
        for (int i = 0, valuesLength = 2; i < valuesLength; i++)
        {
            Object value = values[i];
            QueryExecParam param = new QueryExecParam();
            param.setIndex(buildInfo.getExecInfo().getParams().size());
            param.setType(valueSegment.getType());
            param.setValue(value);
            buildInfo.getExecInfo().getParams().add(param);
        }
    }

    private void processInValues(StringBuilder sb, CompareSegment segment, QueryBuildInfo buildInfo)
    {
        sb.append(" IN (");
        ValueSegment valueSegment = (ValueSegment) segment.getRight();
        Object[] values = (Object[]) valueSegment.getValue();
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++)
        {
            Object value = values[i];
            if (i > 0)
            {
                sb.append(",");
            }
            sb.append("?");

            QueryExecParam param = new QueryExecParam();
            param.setIndex(buildInfo.getExecInfo().getParams().size());
            param.setType(valueSegment.getType());
            param.setValue(value);
            buildInfo.getExecInfo().getParams().add(param);
        }
        sb.append(") ");
    }

    private void processMerge(StringBuilder sb,MergeSegment segment,QueryBuildInfo buildInfo)
    {
        int count = 0;
        for (ISegment subSegment : segment.getSegments())
        {
            if (count > 0)
            {
                switch (segment.getMode())
                {
                    case AND:
                        sb.append(" AND "); break;
                    case OR:
                        sb.append(" OR "); break;
                }
            }
            process(sb,subSegment,buildInfo);
            count++;
        }
    }
}