package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryCondition;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition.AbstractQueryConditionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition.AbstractSqlQueryCondition;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/26/12
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryCondition
{
    private static AbstractQueryConditionFactory factory;

    public static void setFactory(AbstractQueryConditionFactory f)
    {
        factory = f;
    }

    public static IQueryCondition RawSql(String sql)
    {
        AbstractSqlQueryCondition queryCondition = (AbstractSqlQueryCondition) factory.createCondition(
                QueryConditionType.RAW_SQL);
        queryCondition.setSql(sql);
        return queryCondition;
    }
}
