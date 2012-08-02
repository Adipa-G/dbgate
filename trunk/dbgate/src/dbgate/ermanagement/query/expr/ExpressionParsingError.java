package dbgate.ermanagement.query.expr;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExpressionParsingError extends Error
{
    public ExpressionParsingError(String message)
    {
        super(message);
    }

    public ExpressionParsingError(String message, Throwable cause)
    {
        super(message, cause);
    }
}
