package dbgate.exceptions;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 5:44:03 PM
 */
public class DBPatchingException extends DbGateException
{
    public DBPatchingException()
    {
    }

    public DBPatchingException(String s)
    {
        super(s);
    }

    public DBPatchingException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public DBPatchingException(Throwable throwable)
    {
        super(throwable);
    }
}
