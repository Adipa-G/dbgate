package dbgate.ermanagement.query;

import dbgate.IQueryGroup;
import dbgate.exceptions.ExpressionParsingException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.AbstractExpressionGroup;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.AbstractGroupFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.AbstractSqlQueryGroup;
import dbgate.ermanagement.query.expr.GroupExpr;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryGroup
{
    private static AbstractGroupFactory factory;

    public static void setFactory(AbstractGroupFactory f)
    {
        factory = f;
    }

    public static IQueryGroup rawSql(String sql)
    {
        AbstractSqlQueryGroup queryGroup = (AbstractSqlQueryGroup) factory.createGroup(QueryGroupExpressionType.RAW_SQL);
        queryGroup.setSql(sql);
        return queryGroup;
    }
    
    public static IQueryGroup field(Class type, String field) throws ExpressionParsingException
    {
        AbstractExpressionGroup expressionGroup = (AbstractExpressionGroup) factory.createGroup(QueryGroupExpressionType.EXPRESSION);
        expressionGroup.setExpr(GroupExpr.build().field(type,field));
        return expressionGroup;
    }
}
