package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.orderby;

import dbgate.ermanagement.query.IQueryOrderBy;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractOrderBy extends IQueryOrderBy
{
    String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo);
}
