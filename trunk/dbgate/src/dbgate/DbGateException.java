package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:51:00 AM
 */
public class DbGateException extends Exception
{
    public DbGateException()
    {
    }

    public DbGateException(String s)
    {
        super(s);
    }

    public DbGateException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public DbGateException(Throwable throwable)
    {
        super(throwable);
    }
}
