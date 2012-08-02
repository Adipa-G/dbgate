package dbgate.ermanagement.query.expr.segments;

import dbgate.DBColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValueSegment implements ISegment
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
}
