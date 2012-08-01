package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QuerySelectionExpressionType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/1/12
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseColumnOperation implements IAbstractSelection
{
    protected String field;
    protected String alias;
    protected Class type;
    protected String function;

    public Class getType()
    {
        return type;
    }

    public void setType(Class type)
    {
        this.type = type;
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    @Override
    public abstract QuerySelectionExpressionType getSelectionType();

    protected IDBColumn getColumn(QueryBuildInfo buildInfo)
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
    public String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo)
    {
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
    public Object retrieve(ResultSet rs, Connection con,QueryBuildInfo buildInfo) throws RetrievalException
    {
        try
        {
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
