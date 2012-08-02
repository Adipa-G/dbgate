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
}
