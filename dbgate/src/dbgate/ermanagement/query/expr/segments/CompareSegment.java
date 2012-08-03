package dbgate.ermanagement.query.expr.segments;

import dbgate.ermanagement.query.expr.ExpressionParsingError;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompareSegment extends BaseSegment
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

    @Override
    public ISegment add(ISegment segment)
    {
        switch (segment.getSegmentType())
        {
            case FIELD:
            case VALUE:
            case QUERY:
            case GROUP:
                if (left == null)
                {   left = segment; }
                else
                {
                    right = segment;
                }
                return this;
            case MERGE:
                MergeSegment mergeSegment = (MergeSegment)segment;
                if (right != null)
                {   mergeSegment.addSub(this);  }
                else
                {   mergeSegment.setActive(this);   }
                parent = mergeSegment;
                return mergeSegment;
            case COMPARE:
                throw new ExpressionParsingError("Cannot add compare segment to compare segment");
            default:
                return this;
        }
    }
}
