package dbgate.context.impl;

import dbgate.IRelation;
import dbgate.context.EntityFieldValue;
import dbgate.context.ITypeFieldValueList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 22, 2011
 * Time: 9:28:55 PM
 */
public class EntityRelationFieldValueList implements ITypeFieldValueList
{
    private IRelation relation;
    private Collection<EntityFieldValue> fieldValues;

    public EntityRelationFieldValueList(IRelation relation)
    {
        this.relation = relation;
        fieldValues = new ArrayList<EntityFieldValue>();
    }

    public IRelation getRelation()
    {
        return relation;
    }

    @Override
    public Class getType()
    {
        return relation.getRelatedObjectType();
    }

    @Override
    public Collection<EntityFieldValue> getFieldValues()
    {
        return fieldValues;
    }

    @Override
    public EntityFieldValue getFieldValue(String attributeName)
    {
        for (EntityFieldValue fieldValue : fieldValues)
        {
            if (fieldValue.getAttributeName().equals(attributeName))
            {
                return fieldValue;
            }
        }
        return null;
    }
}