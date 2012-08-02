package dbgate.ermanagement.query.expr.segments;

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
    private GroupFunctionSegmentType groupFunctionType;
    private String custFunction;

    public GroupFunctionSegment(GroupFunctionSegmentType groupFunctionType)
    {
        this.groupFunctionType = groupFunctionType;
        this.custFunction = null;
    }

    public GroupFunctionSegment(String custFunction)
    {
        this.groupFunctionType = GroupFunctionSegmentType.CUST_FUNC;
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

    public GroupFunctionSegmentType getGroupFunctionType()
    {
        return groupFunctionType;
    }

    public String getCustFunction()
    {
        return custFunction;
    }
}
