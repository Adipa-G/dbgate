package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group;

import dbgate.ermanagement.IQueryGroup;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAbstractGroup extends IQueryGroup
{
    String createSql();
}
