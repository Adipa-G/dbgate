package dbgate.ermanagement.query.expr;

import dbgate.DBColumnType;
import dbgate.ermanagement.query.expr.segments.*;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;

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

    protected BaseExpr sum()
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentType.SUM);
        return addGroup(segment);
    }

    protected BaseExpr count()
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(GroupFunctionSegmentType.COUNT);
        return addGroup(segment);
    }

    protected BaseExpr custFunc(String func)
    {
        GroupFunctionSegment segment = new GroupFunctionSegment(func);
        return addGroup(segment);
    }

    protected BaseExpr eq()
    {
        CompareSegment segment = new CompareSegment(CompareSegmentType.EQ);
        return addCondition(segment);
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
                throw new ExpressionParsingError("Cannot add field segment to field segment");
            case GROUP:
                ((GroupFunctionSegment)rootSegment).setSegmentToGroup(fieldSegment);
                break;
            case VALUE:
                throw new ExpressionParsingError("Cannot add field segment to value segment");
            case COMPARE:
                ((CompareSegment) rootSegment).setRight(fieldSegment);
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
                throw new ExpressionParsingError("Cannot add value segment to field or group segment");
            case VALUE:
                throw new ExpressionParsingError("Cannot add value segment to value segment");
            case COMPARE:
                ((CompareSegment) rootSegment).setRight(valueSegment);
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
            case GROUP:
                throw new ExpressionParsingError("Cannot add group segment to group segment");
            case VALUE:
                throw new ExpressionParsingError("Cannot add group segment to value segment");
            case COMPARE:
                ((CompareSegment) rootSegment).setRight(groupFunctionSegment);
                break;
        }
        return this;
    }

    private BaseExpr addCondition(CompareSegment compareSegment)
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
        }
        return this;
    }
}
