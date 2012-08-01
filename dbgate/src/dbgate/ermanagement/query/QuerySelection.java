package dbgate.ermanagement.query;

import dbgate.ermanagement.IQuerySelection;
import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuerySelection
{
    private static AbstractSelectionFactory factory;

    public static void setFactory(AbstractSelectionFactory f)
    {
        factory = f;
    }

    public static IQuerySelection rawSql(String sql)
    {
        AbstractSqlQuerySelection querySelection = (AbstractSqlQuerySelection) factory.createSelection(
                QuerySelectionExpressionType.RAW_SQL);
        querySelection.setSql(sql);
        return querySelection;
    }

    public static IQuerySelection type(Class type)
    {
        AbstractTypeSelection selection = (AbstractTypeSelection) factory.createSelection(
                QuerySelectionExpressionType.TYPE);
        selection.setType(type);
        return selection;
    }

    public static IQuerySelection query(ISelectionQuery query)
    {
        return query(query,null);
    }

    public static IQuerySelection query(ISelectionQuery query,String alias)
    {
        AbstractSubQuerySelection selection = (AbstractSubQuerySelection) factory.createSelection(QuerySelectionExpressionType.QUERY);
        selection.setQuery(query);
        if (alias != null && alias.length() > 0)
        {
            selection.setAlias(alias);
        }
        return selection;
    }

    private static IQuerySelection columnOperation(QuerySelectionExpressionType expressionType, Class type,String field,String alias)
    {
        BaseColumnOperation selection = (BaseColumnOperation) factory.createSelection(expressionType);
        selection.setType(type);
        selection.setField(field);
        if (alias != null && alias.length() > 0)
        {
            selection.setAlias(alias);
        }
        return selection;
    }

    public static IQuerySelection column(Class type,String field,String alias)
    {
        return columnOperation(QuerySelectionExpressionType.COLUMN,type,field,alias);
    }

    public static IQuerySelection sum(Class type,String field,String alias)
    {
        return columnOperation(QuerySelectionExpressionType.SUM,type,field,alias);
    }

    public static IQuerySelection count(Class type,String field,String alias)
    {
        return columnOperation(QuerySelectionExpressionType.COUNT,type,field,alias);
    }

    public static IQuerySelection custFunction(String sqlFunction,Class type,String field,String alias)
    {
        AbstractCustFuncSelection selection = (AbstractCustFuncSelection)columnOperation(QuerySelectionExpressionType.CUST_FUNC,type,field,alias);
        selection.setFunction(sqlFunction);
        return selection;
    }
}
