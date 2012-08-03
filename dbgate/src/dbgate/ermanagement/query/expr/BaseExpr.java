package dbgate.ermanagement.query.expr;

import dbgate.DBColumnType;
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
        return  rootSegment;
    }
    
    protected BaseExpr field(Class type,String field)
    {
        FieldSegment segment = new FieldSegment(type,field);
        return addSegment(segment);
    }

    protected BaseExpr field(Class type,String field,String alias)
    {
        FieldSegment segment = new FieldSegment(type,field,alias);
        return addSegment(segment);
    }

    protected BaseExpr value(DBColumnType type,Object value)
    {
        ValueSegment segment = new ValueSegment(type,value);
        return addSegment(segment);
    }

    protected BaseExpr values(DBColumnType type,Object... values)
    {
        ValueSegment segment = new ValueSegment(type,values);
        return addSegment(segment);
    }

    protected BaseExpr sum()
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentMode.SUM);
        return addSegment(segment);
    }

    protected BaseExpr count()
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentMode.COUNT);
        return addSegment(segment);
    }

    protected BaseExpr custFunc(String func)
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(func);
        return addSegment(segment);
    }

    protected BaseExpr eq()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.EQ);
        return addSegment(segment);
    }

    protected BaseExpr ge()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.GE);
        return addSegment(segment);
    }

    protected BaseExpr gt()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.GT);
        return addSegment(segment);
    }

    protected BaseExpr le()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.LE);
        return addSegment(segment);
    }

    protected BaseExpr lt()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.LT);
        return addSegment(segment);
    }

    protected BaseExpr neq()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.NEQ);
        return addSegment(segment);
    }

    protected BaseExpr like()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.LIKE);
        return addSegment(segment);
    }

    protected BaseExpr between()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.BETWEEN);
        return addSegment(segment);
    }

    protected BaseExpr in()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.IN);
        return addSegment(segment);
    }

    protected BaseExpr and()
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.AND);
        return addSegment(mergeSegment);
    }

    protected BaseExpr or()
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.OR);
        return addSegment(mergeSegment);
    }

    protected BaseExpr and(BaseExpr... expressions)
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.PARA_AND);
        for (BaseExpr expression : expressions)
        {
            mergeSegment.addSub(expression.getRootSegment());
        }
        return addSegment(mergeSegment);
    }

    protected BaseExpr or(BaseExpr... expressions)
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.PARA_OR);
        for (BaseExpr expression : expressions)
        {
            mergeSegment.addSub(expression.getRootSegment());
        }
        return addSegment(mergeSegment);
    }

    private BaseExpr addSegment(ISegment segment)
    {
        rootSegment = rootSegment == null
                ? segment
                : rootSegment.add(segment);
        return this;
    }
}
