package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.ISelectionQuery;
import dbgate.ermanagement.exceptions.ExpressionParsingException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryFromExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSubQueryFrom implements IAbstractFrom
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
    public QueryFromExpressionType getFromExpressionType()
    {
        return QueryFromExpressionType.QUERY;
    }

    @Override
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        QueryBuildInfo result = dbLayer.getDataManipulate().processQuery(buildInfo,query.getStructure());
        String sql = "(" + result.getExecInfo().getSql() + ")";
        if (alias != null && alias.length() > 0)
        {
            sql = sql + " as " + alias;
            buildInfo.addQueryAlias(alias,query);
        }
        return sql;
    }
}
