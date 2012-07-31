package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.ermanagement.IQueryFrom;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractQueryFrom extends IQueryFrom
{
    String createSql();
}
