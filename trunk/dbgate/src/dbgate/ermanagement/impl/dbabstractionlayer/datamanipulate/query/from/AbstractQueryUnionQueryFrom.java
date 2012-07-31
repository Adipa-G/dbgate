package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.ermanagement.ISelectionQuery;
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
public class AbstractQueryUnionQueryFrom implements IAbstractQueryFrom
{
    private ISelectionQuery[] queries;
    private boolean all;

    public ISelectionQuery[] getQueries()
    {
        return queries;
    }

    public void setQueries(ISelectionQuery... queries)
    {
        this.queries = queries;
    }

    public boolean isAll()
    {
        return all;
    }

    public void setAll(boolean all)
    {
        this.all = all;
    }

    @Override
    public QueryFromExpressionType getFromExpressionType()
    {
        return QueryFromExpressionType.QUERY_UNION;
    }

    @Override
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo)
    {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("(");
        for (int i = 0, queriesLength = queries.length; i < queriesLength; i++)
        {
            ISelectionQuery query = queries[i];
            QueryBuildInfo result = dbLayer.getDataManipulate().processQuery(buildInfo, query.getStructure());
            if (i > 0)
            {
                sqlBuilder.append( " UNION ");
                if (all)
                {
                    sqlBuilder.append(" ALL ");
                }
            }
            sqlBuilder.append( result.getExecInfo().getSql() + " u_"+i );
        }
        sqlBuilder.append(") src_tbl");
        return sqlBuilder.toString();
    }
}
