package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

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
    public IAbstractSelection createSelection(QuerySelectionExpressionType selectionExpressionType)
    {
        switch (selectionExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQuerySelection();
            case TYPE:
                return new AbstractTypeSelection();
            case QUERY:
                return new AbstractSubQuerySelection();
            case COLUMN:
                return new AbstractColumnSelection();
            case COUNT:
                return new AbstractCountSelection();
            case SUM:
                return new AbstractSumSelection();
            case CUST_FUNC:
                return new AbstractCustFuncSelection();
            default:
                return null;
        }
    }
}
