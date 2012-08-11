package dbgate.ermanagement.query.expr.segments;

import dbgate.ermanagement.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISegment
{
    SegmentType getSegmentType();

    ISegment add(ISegment segment) throws ExpressionParsingException;
}
