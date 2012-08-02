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
    protected ISegment mergeSegment;

    protected BaseExpr()
    {
    }

    public ISegment getRootSegment()
    {
        if (mergeSegment != null)
            return mergeSegment;
        return  rootSegment;
    }
    
    protected BaseExpr field(Class type,String field)
    {
        FieldSegment segment = new FieldSegment(type,field);
        return addField(segment);
    }

    protected BaseExpr field(Class type,String field,String alias)
    {
        FieldSegment segment = new FieldSegment(type,field,alias);
        return addField(segment);
    }

    protected BaseExpr value(DBColumnType type,Object value)
    {
        ValueSegment segment = new ValueSegment(type,value);
        return addValue(segment);
    }

    protected BaseExpr values(DBColumnType type,Object... values)
    {
        ValueSegment segment = new ValueSegment(type,values);
        return addValue(segment);
    }

    protected BaseExpr sum()
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentMode.SUM);
        return addGroup(segment);
    }

    protected BaseExpr count()
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentMode.COUNT);
        return addGroup(segment);
    }

    protected BaseExpr custFunc(String func)
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(func);
        return addGroup(segment);
    }

    protected BaseExpr eq()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.EQ);
        return addCompare(segment);
    }

    protected BaseExpr ge()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.GE);
        return addCompare(segment);
    }

    protected BaseExpr gt()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.GT);
        return addCompare(segment);
    }

    protected BaseExpr le()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.LE);
        return addCompare(segment);
    }

    protected BaseExpr lt()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.LT);
        return addCompare(segment);
    }

    protected BaseExpr neq()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.NEQ);
        return addCompare(segment);
    }

    protected BaseExpr like()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.LIKE);
        return addCompare(segment);
    }

    protected BaseExpr between()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.BETWEEN);
        return addCompare(segment);
    }

    protected BaseExpr in()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentMode.IN);
        return addCompare(segment);
    }

    protected BaseExpr and()
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.AND);
        return addMerge(mergeSegment);
    }

    protected BaseExpr or()
    {
        MergeSegment mergeSegment = new MergeSegment(MergeSegmentMode.OR);
        return addMerge(mergeSegment);
    }

    private BaseExpr addField(FieldSegment fieldSegment)
    {
        if (rootSegment == null)
        {
            rootSegment = fieldSegment;
            return this;
        }

        switch (rootSegment.getSegmentType())
        {
            case FIELD:
            case VALUE:
                throw new ExpressionParsingError("Cannot add field segment to field/value segment");
            case GROUP:
                ((GroupFunctionSegment) rootSegment).setSegmentToGroup(fieldSegment);
                break;
            case COMPARE:
                ((CompareSegment) rootSegment).setRight(fieldSegment);
                if (mergeSegment != null)
                {
                    ((MergeSegment)mergeSegment).addSegment(rootSegment);
                    rootSegment = null;
                }
                break;
            case MERGE:
                mergeSegment = rootSegment;
                rootSegment = fieldSegment;
                break;
        }
        return this;
    }
    
    private BaseExpr addValue(ValueSegment valueSegment)
    {
        if (rootSegment == null)
        {
            rootSegment = valueSegment;
            return this;
        }

        switch (rootSegment.getSegmentType())
        {
            case FIELD:
            case GROUP:
            case VALUE:
                throw new ExpressionParsingError("Cannot add value segment to field/group/value segment");
            case COMPARE:
                ((CompareSegment) rootSegment).setRight(valueSegment);
                if (mergeSegment != null)
                {
                    ((MergeSegment)mergeSegment).addSegment(rootSegment);
                    rootSegment = null;
                }
                break;
            case MERGE:
                mergeSegment = rootSegment;
                rootSegment = valueSegment;
                break;
        }
        return this;
    }

    private BaseExpr addGroup(GroupFunctionSegment groupFunctionSegment)
    {
        if (rootSegment == null)
        {
            rootSegment =  groupFunctionSegment;
            return this;
        }

        switch (rootSegment.getSegmentType())
        {
            case FIELD:
                groupFunctionSegment.setSegmentToGroup((FieldSegment) rootSegment);
                rootSegment = groupFunctionSegment;
                break;
            case COMPARE:
                ((CompareSegment) rootSegment).setRight(groupFunctionSegment);
                if (mergeSegment != null)
                {
                    ((MergeSegment)mergeSegment).addSegment(rootSegment);
                    rootSegment = null;
                }
                break;
            case GROUP:
            case VALUE:
                throw new ExpressionParsingError("Cannot add group segment to value/group segment");
            case MERGE:
                mergeSegment = rootSegment;
                rootSegment = groupFunctionSegment;
                break;
        }
        return this;
    }

    private BaseExpr addCompare(CompareSegment compareSegment)
    {
        if (rootSegment == null)
        {
            throw new ExpressionParsingError("Cannot add comparison without a left value");
        }

        switch (rootSegment.getSegmentType())
        {
            case FIELD:
            case GROUP:
            case VALUE:
                compareSegment.setLeft(rootSegment);
                rootSegment = compareSegment;
                break;
            case COMPARE:
                throw new ExpressionParsingError("Cannot add compare segment to compare segment");
            case MERGE:
                mergeSegment = rootSegment;
                rootSegment = compareSegment;
                break;
        }
        return this;
    }

    private BaseExpr addMerge(MergeSegment mergeSegment)
    {
        if (this.mergeSegment == null)
        {
            this.mergeSegment = mergeSegment;
        }
        else
        {
            mergeSegment.addSegment(this.mergeSegment);
            this.mergeSegment = mergeSegment;
        }

        if (rootSegment == null)
            return this;

        switch (rootSegment.getSegmentType())
        {
            case FIELD:
            case GROUP:
            case VALUE:
            case MERGE:
                throw new ExpressionParsingError("Cannot add merge segment to field/group/value/merge segment");
            case COMPARE:
                this.mergeSegment = mergeSegment;
                mergeSegment.addSegment(rootSegment);
                rootSegment = null;
                break;
        }
        return this;
    }
}
