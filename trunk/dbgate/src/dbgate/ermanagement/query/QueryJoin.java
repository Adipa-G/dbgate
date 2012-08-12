package dbgate.ermanagement.query;

import dbgate.ermanagement.IQueryJoin;
import dbgate.ermanagement.QueryJoinType;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join.AbstractJoinFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join.AbstractSqlQueryJoin;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join.AbstractTypeJoin;
import dbgate.ermanagement.query.expr.JoinExpr;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryJoin
{
    private static AbstractJoinFactory factory;

    public static void setFactory(AbstractJoinFactory f)
    {
        factory = f;
    }

    public static IQueryJoin rawSql(String sql)
    {
        AbstractSqlQueryJoin queryJoin = (AbstractSqlQueryJoin) factory.createOrderBy(QueryJoinExpressionType.RAW_SQL);
        queryJoin.setSql(sql);
        return queryJoin;
    }
    
    public static IQueryJoin type(Class from,Class to)
    {
        return type(from,to,null,null,null);
    }

    public static IQueryJoin type(Class from,Class to,QueryJoinType joinType)
    {
        return type(from,to,null,joinType,null);
    }

    public static IQueryJoin type(Class from,Class to,String alias)
    {
        return type(from,to,null,null,alias);
    }

    public static IQueryJoin type(Class from,Class to,String alias,QueryJoinType joinType)
    {
        return type(from,to,null,joinType,alias);
    }

    public static IQueryJoin type(Class from,Class to,JoinExpr expr)
    {
        return type(from,to,expr,null,null);
    }

    public static IQueryJoin type(Class from,Class to,JoinExpr expr,String alias)
    {
        return type(from,to,expr,null,alias);
    }

    public static IQueryJoin type(Class from,Class to,JoinExpr expr,QueryJoinType joinType,String alias)
    {
        AbstractTypeJoin typeJoin = (AbstractTypeJoin) factory.createOrderBy(QueryJoinExpressionType.TYPE);
        typeJoin.setTypeFrom(from);
        typeJoin.setTypeTo(to);
        if (expr != null)
        {
            typeJoin.setExpr(expr);
        }
        if (joinType != null)
        {
            typeJoin.setJoinType(joinType);
        }
        if (alias != null && !alias.isEmpty())
        {
            typeJoin.setTypeToAlias(alias);
        }
        return typeJoin;
    }
}