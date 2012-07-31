package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join;

import dbgate.ermanagement.IQueryJoin;
import dbgate.ermanagement.IQueryOrderBy;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractQueryJoin extends IQueryJoin
{
    String createSql();
}
