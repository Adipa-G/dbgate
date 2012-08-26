package dbgate.ermanagement.query.expr.segments;

import dbgate.ColumnType;
import dbgate.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValueSegment extends BaseSegment
{
    private ColumnType type;
    private Object value;

    public ValueSegment(ColumnType type, Object value)
    {
        this.type = type;
        this.value = value;
    }

    public ValueSegment(Object value)
    {
        this.value = value;
        Class valueClass = value.getClass();
        if (valueClass.isArray())
        {
            valueClass = ((Object[])value)[0].getClass();
        }
        this.type = ColumnType.getColumnType(valueClass);
    }

    @Override
    public SegmentType getSegmentType()
    {
        return SegmentType.VALUE;
    }

    public ColumnType getType()
    {
        return type;
    }

    public Object getValue()
    {
        return value;
    }

    @Override
    public ISegment add(ISegment segment) throws ExpressionParsingException
    {
        switch (segment.getSegmentType())
        {
            case FIELD:
            case VALUE:
            case QUERY:
            case GROUP:
                throw new ExpressionParsingException("Cannot add field/value/query/merge/group segments to field segment");
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
