package dbgate.ermanagement.query;

import dbgate.ermanagement.IQuerySelection;
import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractSelectionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractSqlQuerySelection;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractSubQuerySelection;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractTypeSelection;

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
}
