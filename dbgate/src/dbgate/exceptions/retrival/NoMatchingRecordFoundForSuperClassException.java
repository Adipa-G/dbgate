package dbgate.exceptions.retrival;

import dbgate.DbGateException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 12:56:19 PM
 */
public class NoMatchingRecordFoundForSuperClassException extends DbGateException
{
    public NoMatchingRecordFoundForSuperClassException(String s)
    {
        super(s);
    }
}
