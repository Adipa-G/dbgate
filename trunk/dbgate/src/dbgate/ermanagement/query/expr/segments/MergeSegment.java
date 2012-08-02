package dbgate.ermanagement.query.expr.segments;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class MergeSegment implements ISegment
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

    public void addSegment(ISegment segment)
    {
        segments.add(segment);
    }

    public Collection<ISegment> getSegments()
    {
        return segments;
    }
}
