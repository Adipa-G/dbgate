package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:51:00 AM
 */
public class BaseException extends Exception
{
    public BaseException()
    {
    }

    public BaseException(String s)
    {
        super(s);
    }

    public BaseException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public BaseException(Throwable throwable)
    {
        super(throwable);
    }
}
