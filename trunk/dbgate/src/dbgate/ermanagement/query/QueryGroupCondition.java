package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryGroup;
import dbgate.ermanagement.IQueryGroupCondition;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.groupcondition.AbstractQueryGroupConditionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.groupcondition.AbstractSqlQueryGroupCondition;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryGroupCondition
{
    private static AbstractQueryGroupConditionFactory factory;

    public static void setFactory(AbstractQueryGroupConditionFactory f)
    {
        factory = f;
    }

    public static IQueryGroupCondition RawSql(String sql)
    {
        AbstractSqlQueryGroupCondition queryGroup = (AbstractSqlQueryGroupCondition) factory.createGroupCondition(QueryGroupConditionExpressionType.RAW_SQL);
        queryGroup.setSql(sql);
        return queryGroup;
    }
}
