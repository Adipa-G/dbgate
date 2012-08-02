package dbgate.ermanagement.query.expr.segments;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class FieldSegment implements ISegment
{
    private Class type;
    private String field;
    private String alias;

    public FieldSegment(Class type, String field)
    {
        this.type = type;
        this.field = field;
    }

    public FieldSegment(Class type, String field, String alias)
    {
        this.type = type;
        this.field = field;
        this.alias = alias;
    }

    @Override
    public SegmentType getSegmentType()
    {
        return SegmentType.FIELD;
    }

    public Class getType()
    {
        return type;
    }

    public String getField()
    {
        return field;
    }

    public String getAlias()
    {
        return alias;
    }
}
