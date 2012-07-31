package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby;

import dbgate.ermanagement.IQueryOrderBy;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractQueryOrderBy extends IQueryOrderBy
{
    String createSql();
}
