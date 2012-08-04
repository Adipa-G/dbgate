package dbgate.ermanagement.query.expr.segments;

import dbgate.ermanagement.query.expr.ExpressionParsingError;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class MergeSegment extends BaseSegment
{
    private Collection<ISegment> segments;
    private MergeSegmentMode mode;

    public MergeSegment(MergeSegmentMode mode)
    {
        segments = new ArrayList<>();
        this.mode = mode;
    }

    @Override
    public SegmentType getSegmentType()
    {
        return SegmentType.MERGE;
    }

    public MergeSegmentMode getMode()
    {
        return mode;
    }

    public void addSub(ISegment segment)
    {
        segments.add(segment);
    }

    public Collection<ISegment> getSegments()
    {
        return segments;
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
            case COMPARE:
                ISegment result = active != null ? active.add(segment) : segment;
                if (result.getSegmentType() == SegmentType.COMPARE
                        && ((CompareSegment)result).getRight() != null)
                {
                    addSub(result);
                    ((CompareSegment) result).setParent(this);
                    active = null;
                }
                else
                {
                    active = result;
                }
                return this;
            case MERGE:
                MergeSegment mergeSegment = (MergeSegment)segment;
                mergeSegment.addSub(this);
                parent = mergeSegment;
                return mergeSegment;
            default:
                return this;
        }
    }
}
