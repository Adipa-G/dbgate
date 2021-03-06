package dbgate.ermanagement.query.expr.segments;

import dbgate.exceptions.ExpressionParsingException;

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
    public ISegment add(ISegment segment) throws ExpressionParsingException
    {
        switch (segment.getSegmentType())
        {
            case FIELD:
            case VALUE:
            case QUERY:
                if (left == null
                    && !(mode == CompareSegmentMode.EXISTS || mode == CompareSegmentMode.NOT_EXISTS))
                {
                    left = segment;
                }
                else
                {
                    right = segment;
                }
                return this;
            case GROUP:
                GroupFunctionSegment groupFunctionSegment = (GroupFunctionSegment) segment;
                if (groupFunctionSegment.getSegmentToGroup() == null)
                {
                    if (right != null && right.getSegmentType() == SegmentType.FIELD)
                    {
                        groupFunctionSegment.setSegmentToGroup((FieldSegment) right);
                        right = groupFunctionSegment;
                    }
                    else if (left != null && left.getSegmentType() == SegmentType.FIELD)
                    {
                        groupFunctionSegment.setSegmentToGroup((FieldSegment) left);
                        left = groupFunctionSegment;
                    }
                }
                else
                {
                    if (left == null
                            && !(mode == CompareSegmentMode.EXISTS || mode == CompareSegmentMode.NOT_EXISTS))
                    {
                        left = segment;
                    }
                    else
                    {
                        right = segment;
                    }
                }
                return this;
            case MERGE:
                segment.add(this);
                parent = segment;
                return segment;
            case COMPARE:
                throw new ExpressionParsingException("Cannot add compare segment to compare segment");
            default:
                return this;
        }
    }
}
