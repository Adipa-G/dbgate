package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 12:56:19 PM
 */
public class NoMatchingRecordFoundForSuperClassException extends BaseException
{
    public NoMatchingRecordFoundForSuperClassException()
    {
    }

    public NoMatchingRecordFoundForSuperClassException(String s)
    {
        super(s);
    }

    public NoMatchingRecordFoundForSuperClassException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public NoMatchingRecordFoundForSuperClassException(Throwable throwable)
    {
        super(throwable);
    }
}
