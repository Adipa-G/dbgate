package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join;

import dbgate.ermanagement.exceptions.ExpressionParsingException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryJoinExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSqlQueryJoin implements IAbstractJoin
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
    public QueryJoinExpressionType getJoinExpressionType()
    {
        return QueryJoinExpressionType.RAW_SQL;
    }

    @Override
    public String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        return sql;
    }
}
