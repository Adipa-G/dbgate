package dbgate.ermanagement.query.expr.segments;

import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuerySegment implements ISegment
{
    private String alias;
    private ISelectionQuery query;

    public QuerySegment(ISelectionQuery query, String alias)
    {
        this.query = query;
        this.alias = alias;
    }

    @Override
    public SegmentType getSegmentType()
    {
        return SegmentType.QUERY;
    }

    public String getAlias()
    {
        return alias;
    }

    public ISelectionQuery getQuery()
    {
        return query;
    }

    @Override
    public ISegment add(ISegment segment) throws ExpressionParsingException
    {
        switch (segment.getSegmentType())
        {
            case FIELD:
            case VALUE:
            case QUERY:
            case GROUP:
                throw new ExpressionParsingException("Cannot add field/value/query/group segments to field segment");
            case MERGE:
                segment.add(this);
                return segment;
            case COMPARE:
                segment.add(this);
                return segment;
            default:
                return this;
        }
    }
}
