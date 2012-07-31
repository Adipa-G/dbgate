package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition;

import dbgate.ermanagement.IQueryCondition;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractQueryCondition extends IQueryCondition
{
    String createSql();
}
