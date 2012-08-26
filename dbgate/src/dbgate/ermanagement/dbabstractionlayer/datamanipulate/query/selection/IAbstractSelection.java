package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ITransaction;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.IQuerySelection;
import dbgate.exceptions.ExpressionParsingException;
import dbgate.exceptions.RetrievalException;

import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractSelection extends IQuerySelection
{
    String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo) throws ExpressionParsingException;

    Object retrieve(ResultSet rs,ITransaction tx,QueryBuildInfo buildInfo) throws RetrievalException;
}
