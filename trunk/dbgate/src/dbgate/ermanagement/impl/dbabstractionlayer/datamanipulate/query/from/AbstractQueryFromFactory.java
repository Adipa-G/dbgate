package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.ermanagement.query.QueryFromType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractQueryFromFactory
{
    public IAbstractQueryFrom createFrom(QueryFromType fromType)
    {
        switch (fromType)
        {
            case RAW_SQL:
                return new AbstractSqlQueryFrom();
            default:
                return null;
        }
    }
}
