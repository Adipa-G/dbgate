package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 3:38:06 PM
 */
public class NoSetterFoundToSetChildObjectListException extends BaseException
{
    public NoSetterFoundToSetChildObjectListException()
    {
    }

    public NoSetterFoundToSetChildObjectListException(String s)
    {
        super(s);
    }

    public NoSetterFoundToSetChildObjectListException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public NoSetterFoundToSetChildObjectListException(Throwable throwable)
    {
        super(throwable);
    }
}
