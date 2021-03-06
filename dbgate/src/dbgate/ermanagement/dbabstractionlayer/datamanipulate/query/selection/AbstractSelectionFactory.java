package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ermanagement.query.QuerySelectionExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractSelectionFactory
{
    public AbstractSelectionFactory()
    {
    }

    public IAbstractSelection createSelection(QuerySelectionExpressionType selectionExpressionType)
    {
        switch (selectionExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQuerySelection();
            case TYPE:
                return new AbstractTypeSelection();
            case EXPRESSION:
                return new AbstractExpressionSelection();
            default:
                return null;
        }
    }
}
