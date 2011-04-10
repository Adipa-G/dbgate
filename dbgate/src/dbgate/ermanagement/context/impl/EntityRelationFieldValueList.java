package dbgate.ermanagement.context.impl;

import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.context.EntityFieldValue;
import dbgate.ermanagement.context.ITypeFieldValueList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 22, 2011
 * Time: 9:28:55 PM
 */
public class EntityRelationFieldValueList implements ITypeFieldValueList
{
    private IDBRelation relation;
    private Collection<EntityFieldValue> fieldValues;

    public EntityRelationFieldValueList(IDBRelation relation)
    {
        this.relation = relation;
        fieldValues = new ArrayList<EntityFieldValue>();
    }

    public IDBRelation getRelation()
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