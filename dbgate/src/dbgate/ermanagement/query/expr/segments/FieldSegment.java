package dbgate.ermanagement.query.expr.segments;

import dbgate.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class FieldSegment extends BaseSegment
{
    private Class type;
    private String typeAlias;
    private String field;
    private String alias;

    public FieldSegment(String field)
    {
        this.field = field;
    }

    public FieldSegment(String field, String alias)
    {
        this.field = field;
        this.alias = alias;
    }

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

    public FieldSegment(Class type, String typeAlias, String field, String alias)
    {
        this.type = type;
        this.typeAlias = typeAlias;
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

    public String getTypeAlias()
    {
        return typeAlias;
    }

    public String getField()
    {
        return field;
    }

    public String getAlias()
    {
        return alias;
    }

    @Override
    public ISegment add(ISegment segment) throws ExpressionParsingException
    {
        switch (segment.getSegmentType())
        {
            case FIELD:
            case VALUE:
            case QUERY:
                throw new ExpressionParsingException("Cannot add field/value/query segments to field segment");
            case MERGE:
                segment.add(this);
                return segment;
            case GROUP:
            case COMPARE:
                segment.add(this);
                parent = segment;
                return segment;
            default:
                return this;
        }
    }
}
