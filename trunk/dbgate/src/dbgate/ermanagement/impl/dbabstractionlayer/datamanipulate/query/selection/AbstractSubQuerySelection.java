package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QuerySelectionExpressionType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSubQuerySelection implements IAbstractSelection
{
    private ISelectionQuery query;
    private String alias;

    public ISelectionQuery getQuery()
    {
        return query;
    }

    public void setQuery(ISelectionQuery query)
    {
        this.query = query;
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
    public QuerySelectionExpressionType getSelectionType()
    {
        return QuerySelectionExpressionType.QUERY;
    }

    @Override
    public String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo)
    {
        QueryBuildInfo result = dbLayer.getDataManipulate().processQuery(buildInfo,query.getStructure());
        String sql = "(" + result.getExecInfo().getSql() + ")";
        if (alias == null || alias.length() == 0)
        {
            alias = "col_" + UUID.randomUUID().toString().substring(0,5);
        }
        sql = sql + " as " + alias;
        buildInfo.addQueryAlias(alias,query);
        return sql;
    }

    public Object retrieve(ResultSet rs,Connection con,QueryBuildInfo buildInfo) throws RetrievalException
    {
        try
        {
            Object obj = rs.getObject(alias);
            return obj;
        }
        catch (Exception ex)
        {
            throw new RetrievalException(ex.getMessage(),ex);
        }
    }
}
