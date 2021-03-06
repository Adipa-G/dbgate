package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.group;

import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryGroupExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSqlQueryGroup implements IAbstractGroup
{
    protected String sql;

    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql = sql;
    }

    @Override
    public QueryGroupExpressionType getGroupExpressionType()
    {
        return QueryGroupExpressionType.RAW_SQL;
    }

    @Override
    public String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo)
    {
        return sql;
    }
}
