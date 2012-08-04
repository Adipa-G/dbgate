package dbgate.ermanagement.query.expr.segments;

import dbgate.DBColumnType;
import dbgate.ermanagement.query.expr.ExpressionParsingError;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValueSegment extends BaseSegment
{
    private DBColumnType type;
    private Object value;

    public ValueSegment(DBColumnType type, Object value)
    {
        this.type = type;
        this.value = value;
    }

    @Override
    public SegmentType getSegmentType()
    {
        return SegmentType.VALUE;
    }

    public DBColumnType getType()
    {
        return type;
    }

    public Object getValue()
    {
        return value;
    }

    @Override
    public ISegment add(ISegment segment)
    {
        switch (segment.getSegmentType())
        {
            case FIELD:
            case VALUE:
            case QUERY:
            case GROUP:
                throw new ExpressionParsingError("Cannot add field/value/query/merge/group segments to field segment");
            case MERGE:
                segment.add(this);
                return segment;
            case COMPARE:
                segment.add(this);
                parent = segment;
                return segment;
            default:
                return this;
        }
    }
}
