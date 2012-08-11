package dbgate.ermanagement.exceptions.query;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 12:28:43 PM
 */
public class QueryBuildingException extends DbGateException
{
    public QueryBuildingException()
    {
    }

    public QueryBuildingException(String s)
    {
        super(s);
    }

    public QueryBuildingException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public QueryBuildingException(Throwable throwable)
    {
        super(throwable);
    }
}
