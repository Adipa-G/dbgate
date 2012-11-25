package dbgate.exceptions.retrival;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 3:38:06 PM
 */
public class NoSetterFoundToSetChildObjectListException extends DbGateException
{
    public NoSetterFoundToSetChildObjectListException(String s)
    {
        super(s);
    }
}
