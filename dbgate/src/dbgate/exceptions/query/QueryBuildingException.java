package dbgate.exceptions.query;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 12:28:43 PM
 */
public class QueryBuildingException extends DbGateException
{
    public QueryBuildingException(String s)
    {
        super(s);
    }
}
