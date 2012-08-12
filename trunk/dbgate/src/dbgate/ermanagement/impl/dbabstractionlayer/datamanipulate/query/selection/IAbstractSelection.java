package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.IQuerySelection;
import dbgate.ermanagement.exceptions.ExpressionParsingException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;

import java.sql.Connection;
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

    Object retrieve(ResultSet rs,Connection con,QueryBuildInfo buildInfo) throws RetrievalException;
}
