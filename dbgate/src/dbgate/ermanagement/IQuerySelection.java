package dbgate.ermanagement;

import dbgate.ermanagement.exceptions.RetrievalException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IQuerySelection
{
    Object retrieve(ResultSet rs) throws SQLException;
}
