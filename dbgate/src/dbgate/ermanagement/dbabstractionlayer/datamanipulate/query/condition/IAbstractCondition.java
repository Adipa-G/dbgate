package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.condition;

import dbgate.ermanagement.query.IQueryCondition;
import dbgate.exceptions.ExpressionParsingException;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractCondition extends IQueryCondition
{
    String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo) throws ExpressionParsingException;
}
