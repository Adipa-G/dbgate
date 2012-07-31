package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group;

import dbgate.ermanagement.query.QueryGroupType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractQueryGroupFactory
{
    public IAbstractQueryGroup createGroup(QueryGroupType groupType)
    {
        switch (groupType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryGroup();
            default:
                return null;
        }
    }
}
