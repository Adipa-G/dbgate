package dbgate.ermanagement.query.expr.segments;

import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.query.expr.ExpressionParsingError;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuerySegment implements ISegment
{
    private ISelectionQuery query;

    public QuerySegment(ISelectionQuery query)
    {
        this.query = query;
    }

    @Override
    public SegmentType getSegmentType()
    {
        return SegmentType.QUERY;
    }

    public ISelectionQuery getQuery()
    {
        return query;
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
                throw new ExpressionParsingError("Cannot add field/value/query/group segments to field segment");
            case MERGE:
                MergeSegment mergeSegment = (MergeSegment)segment;
                mergeSegment.setActive(this);
                return mergeSegment;
            case COMPARE:
                CompareSegment compareSegment = (CompareSegment) segment;
                if (compareSegment.getLeft() == null)
                {   compareSegment.setLeft(this);   }
                else
                {   compareSegment.setRight(this);  }
                return compareSegment;
            default:
                return this;
        }
    }
}
