package dbgate.persist.support.columntest;

import dbgate.ISequenceGenerator;
import dbgate.ITransaction;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 10, 2010
 * Time: 7:06:39 PM
 */
public class PrimaryKeyGenerator implements ISequenceGenerator
{
    public Object getNextSequenceValue(ITransaction tx)
    {
        return 35;
    }
}
