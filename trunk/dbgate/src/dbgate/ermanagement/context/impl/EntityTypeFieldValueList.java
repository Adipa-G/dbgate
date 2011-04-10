package dbgate.ermanagement.context.impl;

import dbgate.ermanagement.context.EntityFieldValue;
import dbgate.ermanagement.context.ITypeFieldValueList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 22, 2011
 * Time: 9:28:55 PM
 */
public class EntityTypeFieldValueList implements ITypeFieldValueList
{
    private Class type;
    private Collection<EntityFieldValue> fieldValues;

    public EntityTypeFieldValueList(Class type)
    {
        this.type = type;
        fieldValues = new ArrayList<EntityFieldValue>();
    }

    @Override
    public Class getType()
    {
        return type;
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