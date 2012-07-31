package dbgate.ermanagement.query;

import dbgate.ermanagement.IQuerySelection;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractQuerySelectionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractSqlQuerySelection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuerySelection
{
    private static AbstractQuerySelectionFactory factory;

    public static void setFactory(AbstractQuerySelectionFactory f)
    {
        factory = f;
    }

    public static IQuerySelection RawSql(String sql)
    {
        AbstractSqlQuerySelection querySelection = (AbstractSqlQuerySelection) factory.createSelection(
                QuerySelectionExpressionType.RAW_SQL);
        querySelection.setSql(sql);
        return querySelection;
    }
}
