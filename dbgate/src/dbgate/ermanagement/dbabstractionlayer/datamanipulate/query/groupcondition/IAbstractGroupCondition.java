package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.groupcondition;

import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.IQueryGroupCondition;
import dbgate.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractGroupCondition extends IQueryGroupCondition
{
    String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo) throws ExpressionParsingException;
}
