package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:59:54 AM
 */
public class SequenceGeneratorInitializationException extends BaseException
{
    public SequenceGeneratorInitializationException()
    {
    }

    public SequenceGeneratorInitializationException(String s)
    {
        super(s);
    }

    public SequenceGeneratorInitializationException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public SequenceGeneratorInitializationException(Throwable throwable)
    {
        super(throwable);
    }
}
