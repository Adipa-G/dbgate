package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ermanagement.query.QuerySelectionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractQuerySelectionFactory
{
    public IAbstractQuerySelection createSelection(QuerySelectionType selectionType)
    {
        switch (selectionType)
        {
            case RAW_SQL:
                return new AbstractSqlQuerySelection();
            default:
                return null;
        }
    }
}
