package dbgate.ermanagement;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 10, 2010
 * Time: 6:35:20 PM
 */
public interface ISequenceGenerator
{
    abstract Object getNextSequenceValue(Connection con);
}