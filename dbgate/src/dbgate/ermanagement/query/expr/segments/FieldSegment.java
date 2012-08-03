package dbgate.ermanagement.query.expr.segments;

import dbgate.ermanagement.query.expr.ExpressionParsingError;

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

    @Override
    public ISegment add(ISegment segment)
    {
        switch (segment.getSegmentType())
        {
            case FIELD:
            case VALUE:
            case QUERY:
                throw new ExpressionParsingError("Cannot add field/value/query segments to field segment");
            case MERGE:
                MergeSegment mergeSegment = (MergeSegment)segment;
                mergeSegment.setActive(this);
                return mergeSegment;
            case GROUP:
                ((GroupFunctionSegment)segment).setSegmentToGroup(this);
                parent = segment;
                return segment;
            case COMPARE:
                CompareSegment compareSegment = (CompareSegment) segment;
                if (compareSegment.getLeft() == null)
                {   compareSegment.setLeft(this);   }
                else
                {   compareSegment.setRight(this);  }
                parent = compareSegment;
                return compareSegment;
            default:
                return this;
        }
    }
}
