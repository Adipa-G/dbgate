package dbgate.ermanagement.support.persistant.columntest;

import dbgate.ermanagement.ISequenceGenerator;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 10, 2010
 * Time: 7:06:39 PM
 */
public class PrimaryKeyGenerator implements ISequenceGenerator
{
    public Object getNextSequenceValue(Connection con)
    {
        return 35;
    }
}
