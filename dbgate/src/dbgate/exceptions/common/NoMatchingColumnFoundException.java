package dbgate.exceptions.common;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 5:31:27 PM
 */
public class NoMatchingColumnFoundException extends DbGateException
{
    public NoMatchingColumnFoundException(String s)
    {
        super(s);
    }
}
