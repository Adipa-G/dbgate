package dbgate.ermanagement.query.expr;

import dbgate.ColumnType;
import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.exceptions.ExpressionParsingException;
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

    protected BaseExpr field(String field) throws ExpressionParsingException
    {
        FieldSegment segment = new FieldSegment(field);
        return addSegment(segment);
    }

    protected BaseExpr field(String field,String alias) throws ExpressionParsingException
    {
        FieldSegment segment = new FieldSegment(field,alias);
        return addSegment(segment);
    }
    
    protected BaseExpr field(Class type,String field) throws ExpressionParsingException
    {
        FieldSegment segment = new FieldSegment(type,field);
        return addSegment(segment);
    }

    protected BaseExpr field(Class type,String field,String alias) throws ExpressionParsingException
    {
        FieldSegment segment = new FieldSegment(type,field,alias);
        return addSegment(segment);
    }

    protected BaseExpr field(Class type,String typeAlias,String field,String alias) throws ExpressionParsingException
    {
        FieldSegment segment = new FieldSegment(type,typeAlias,field,alias);
        return addSegment(segment);
    }

    protected BaseExpr value(ColumnType type,Object value) throws ExpressionParsingException
    {
        ValueSegment segment = new ValueSegment(type,value);
        return addSegment(segment);
    }

    protected BaseExpr values(ColumnType type,Object... values) throws ExpressionParsingException
    {
        ValueSegment segment = new ValueSegment(type,values);
        return addSegment(segment);
    }

    protected BaseExpr query(ISelectionQuery query) throws ExpressionParsingException
    {
        return query(query,null);
    }

    protected BaseExpr query(ISelectionQuery query,String alias) throws ExpressionParsingException
    {
        QuerySegment segment = new QuerySegment(query,alias);
        return addSegment(segment);
    }

    protected BaseExpr sum() throws ExpressionParsingException
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentMode.SUM);
        return addSegment(segment);
    }

    protected BaseExpr count() throws ExpressionParsingException
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentMode.COUNT);
        return addSegment(segment);
    }

    protected BaseExpr custFunc(String func) throws ExpressionParsingException
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(func);
        return addSegment(segment);
    }

    protected BaseExpr eq() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.EQ);
        return addSegment(segment);
    }

    protected BaseExpr ge() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.GE);
        return addSegment(segment);
    }

    protected BaseExpr gt() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.GT);
        return addSegment(segment);
    }

    protected BaseExpr le() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.LE);
        return addSegment(segment);
    }

    protected BaseExpr lt() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.LT);
        return addSegment(segment);
    }

    protected BaseExpr neq() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.NEQ);
        return addSegment(segment);
    }

    protected BaseExpr like() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.LIKE);
        return addSegment(segment);
    }

    protected BaseExpr between() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.BETWEEN);
        return addSegment(segment);
    }

    protected BaseExpr in() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.IN);
        return addSegment(segment);
    }

    protected BaseExpr exists() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.EXISTS);
        return addSegment(segment);
    }

    protected BaseExpr notExists() throws ExpressionParsingException
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.NOT_EXISTS);
        return addSegment(segment);
    }

    protected BaseExpr and() throws ExpressionParsingException
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.AND);
        return addSegment(mergeSegment);
    }

    protected BaseExpr or() throws ExpressionParsingException
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.OR);
        return addSegment(mergeSegment);
    }

    protected BaseExpr and(BaseExpr... expressions) throws ExpressionParsingException
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.PARA_AND);
        for (BaseExpr expression : expressions)
        {
            mergeSegment.addSub(expression.getRootSegment());
        }
        return addSegment(mergeSegment);
    }

    protected BaseExpr or(BaseExpr... expressions) throws ExpressionParsingException
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.PARA_OR);
        for (BaseExpr expression : expressions)
        {
            mergeSegment.addSub(expression.getRootSegment());
        }
        return addSegment(mergeSegment);
    }

    private BaseExpr addSegment(ISegment segment) throws ExpressionParsingException
    {
        rootSegment = rootSegment == null
                ? segment
                : rootSegment.add(segment);
        return this;
    }
}
