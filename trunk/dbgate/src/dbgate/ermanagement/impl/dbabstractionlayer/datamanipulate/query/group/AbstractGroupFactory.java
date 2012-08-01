package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group;

import dbgate.ermanagement.query.QueryGroupExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractGroupFactory
{
    public IAbstractGroup createGroup(QueryGroupExpressionType groupExpressionType)
    {
        switch (groupExpressionType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryGroup();
            default:
                return null;
        }
    }
}
