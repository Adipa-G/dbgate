package dbgate.ermanagement.query.expr;

import dbgate.ermanagement.query.expr.segments.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/2/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
class BaseExpr
{
    protected ISegment rootSegment;

    protected BaseExpr()
    {
    }

    public ISegment getRootSegment()
    {
        return rootSegment;
    }

    protected BaseExpr sum()
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentType.SUM);
        return add(segment);
    }

    protected BaseExpr count()
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentType.COUNT);
        return add(segment);
    }

    protected BaseExpr custFunc(String func)
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(func);
        return add(segment);
    }

    protected BaseExpr field(Class type,String field)
    {
        FieldSegment segment = new FieldSegment(type,field);
        return add(segment);
    }

    protected BaseExpr field(Class type,String field,String alias)
    {
        FieldSegment segment = new FieldSegment(type,field,alias);
        return add(segment);
    }

    private BaseExpr add(ISegment segmentToAdd)
    {
        if (rootSegment == null)
        {
            rootSegment = segmentToAdd;
            return this;
        }

        switch (rootSegment.getSegmentType())
        {
            case FIELD:
                if (segmentToAdd.getSegmentType() == SegmentType.GROUP)
                {
                    ((GroupFunctionSegment)segmentToAdd).setSegmentToGroup(rootSegment);
                    rootSegment = segmentToAdd;
                }
                else
                {
                    throw new ExpressionParsingError(String.format("Cannot add segment type %s to field segment",segmentToAdd.getSegmentType()));
                }
                break;
            case GROUP:
                if (segmentToAdd.getSegmentType() == SegmentType.FIELD)
                {
                    ((GroupFunctionSegment)rootSegment).setSegmentToGroup(segmentToAdd);
                }
                else
                {
                    throw new ExpressionParsingError(String.format("Cannot add segment type %s to group segment",segmentToAdd.getSegmentType()));
                }
                break;
            case VALUE:
                throw new ExpressionParsingError(String.format("Cannot add segment type %s to value segment",segmentToAdd.getSegmentType()));
            case COMPARE:
                ((CompareSegment) rootSegment).setRight(segmentToAdd);
                break;
        }
        return this;
    }
}
