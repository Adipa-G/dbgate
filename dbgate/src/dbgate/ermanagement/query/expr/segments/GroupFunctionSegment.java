package dbgate.ermanagement.query.expr.segments;

import dbgate.ermanagement.query.expr.ExpressionParsingError;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroupFunctionSegment implements ISegment
{
    private FieldSegment segmentToGroup;
    private GroupFunctionSegmentMode groupFunctionMode;
    private String custFunction;

    public GroupFunctionSegment(GroupFunctionSegmentMode groupFunctionMode)
    {
        this.groupFunctionMode = groupFunctionMode;
        this.custFunction = null;
    }

    public GroupFunctionSegment(String custFunction)
    {
        this.groupFunctionMode = GroupFunctionSegmentMode.CUST_FUNC;
        this.custFunction = custFunction;
    }

    public FieldSegment getSegmentToGroup()
    {
        return segmentToGroup;
    }

    public void setSegmentToGroup(FieldSegment segmentToGroup)
    {
        this.segmentToGroup = segmentToGroup;
    }

    @Override
    public SegmentType getSegmentType()
    {
        return SegmentType.GROUP;
    }

    public GroupFunctionSegmentMode getGroupFunctionMode()
    {
        return groupFunctionMode;
    }

    public String getCustFunction()
    {
        return custFunction;
    }

    @Override
    public ISegment add(ISegment segment)
    {
        switch (segment.getSegmentType())
        {
            case FIELD:
                segmentToGroup = (FieldSegment)segment;
                return this;
            case VALUE:
            case QUERY:
            case MERGE:
            case GROUP:
                throw new ExpressionParsingError("Cannot add value/query/merge/group segments to field segment");
            case COMPARE:
                segment.add(this);
                return segment;
            default:
                return this;
        }
    }
}
