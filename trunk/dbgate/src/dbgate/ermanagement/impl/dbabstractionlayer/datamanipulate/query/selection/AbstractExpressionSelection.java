package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QuerySelectionExpressionType;
import dbgate.ermanagement.query.expr.SelectExpr;
import dbgate.ermanagement.query.expr.segments.*;

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
public class AbstractExpressionSelection implements IAbstractSelection
{
    private SelectExpr expr;

    private Class type;
    private String field;
    private String alias;
    private String function;

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

    private void processExpr()
    {
        if (type != null)
            return;
        
        ISegment rootSegment = expr.getRootSegment();
        GroupFunctionSegment groupSegment = null;
        FieldSegment fieldSegment = null;

        if (rootSegment.getSegmentType() == SegmentType.GROUP)
        {
            groupSegment = (GroupFunctionSegment) rootSegment;
            fieldSegment = (FieldSegment) groupSegment.getSegmentToGroup();
        }
        if (rootSegment.getSegmentType() == SegmentType.FIELD)
        {
            fieldSegment = (FieldSegment) rootSegment;
        }

        if (fieldSegment != null)
        {
            this.type = fieldSegment.getType();
            this.field = fieldSegment.getField();
            this.alias = fieldSegment.getAlias();
        }

        if (groupSegment != null)
        {
            switch (groupSegment.getGroupFunctionType())
            {
                case COUNT:
                    function = "COUNT";
                    break;
                case SUM:
                    function = "SUM";
                    break;
                case CUST_FUNC:
                    function = groupSegment.getCustFunction();
                    break;
            }
        }
    }

    private IDBColumn getColumn(QueryBuildInfo buildInfo)
    {
        Collection<IDBColumn> columns = null;
        try
        {
            columns = CacheManager.fieldCache.getColumns(type);
        }
        catch (FieldCacheMissException e)
        {
            try
            {
                CacheManager.fieldCache.register(type,(ServerRODBClass) type.newInstance());
                columns = CacheManager.fieldCache.getColumns(type);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        for (IDBColumn column : columns)
        {
            if (column.getAttributeName().equals(field))
            {
                return column;
            }
        }
        return null;
    }

    @Override
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo)
    {
        processExpr();
        String tableAlias = buildInfo.getAlias(type);
        tableAlias = (tableAlias == null)?"" : tableAlias + ".";
        IDBColumn column = getColumn(buildInfo);

        if (column != null)
        {
            String sql = "";
            if (function != null && function.length() > 0)
            {
                sql = function + "(" +tableAlias + column.getColumnName()+ ")";
            }
            else
            {
                sql = tableAlias + column.getColumnName();
            }
            if (alias != null && alias.length() > 0)
            {
                sql = sql + " AS " + alias;
            }
            return sql;
        }
        else
        {
            return "<incorrect column for " + field + ">";
        }
    }

    @Override
    public Object retrieve(ResultSet rs, Connection con, QueryBuildInfo buildInfo) throws RetrievalException
    {
        try
        {
            processExpr();
            String columnName = alias != null && alias.length() > 0? alias : getColumn(buildInfo).getColumnName();
            Object obj = rs.getObject(columnName);
            return obj;
        }
        catch (Exception ex)
        {
            throw new RetrievalException(ex.getMessage(),ex);
        }
    }
}
