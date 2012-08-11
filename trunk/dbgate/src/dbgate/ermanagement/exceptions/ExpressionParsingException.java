package dbgate.ermanagement.exceptions;

import dbgate.BaseException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExpressionParsingException extends BaseException
{
    public ExpressionParsingException()
    {
    }

    public ExpressionParsingException(String s)
    {
        super(s);
    }

    public ExpressionParsingException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public ExpressionParsingException(Throwable throwable)
    {
        super(throwable);
    }
}
