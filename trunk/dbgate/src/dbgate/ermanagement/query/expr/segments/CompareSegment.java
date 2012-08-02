package dbgate.ermanagement.query.expr.segments;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompareSegment implements ISegment
{
    private ISegment left;
    private ISegment right;
    private CompareSegmentMode mode;

    public CompareSegment(CompareSegmentMode mode)
    {
        this.mode = mode;
    }

    @Override
    public SegmentType getSegmentType()
    {
        return SegmentType.COMPARE;
    }

    public ISegment getLeft()
    {
        return left;
    }

    public void setLeft(ISegment left)
    {
        this.left = left;
    }

    public ISegment getRight()
    {
        return right;
    }

    public void setRight(ISegment right)
    {
        this.right = right;
    }

    public CompareSegmentMode getMode()
    {
        return mode;
    }
}
