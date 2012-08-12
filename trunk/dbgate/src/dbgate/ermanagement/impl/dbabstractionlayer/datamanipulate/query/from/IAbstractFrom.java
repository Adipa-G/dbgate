package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.IQueryFrom;
import dbgate.ermanagement.exceptions.ExpressionParsingException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractFrom extends IQueryFrom
{
    String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo) throws ExpressionParsingException;
}
