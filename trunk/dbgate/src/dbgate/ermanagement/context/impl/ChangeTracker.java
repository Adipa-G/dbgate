package dbgate.ermanagement.context.impl;

import dbgate.ermanagement.context.EntityFieldValue;
import dbgate.ermanagement.context.IChangeTracker;
import dbgate.ermanagement.context.ITypeFieldValueList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 23, 2011
 * Time: 9:27:23 PM
 */
public class ChangeTracker implements IChangeTracker
{
    private Collection<EntityFieldValue> fields;
    private Collection<ITypeFieldValueList> childEntityRelationKeys;

    public ChangeTracker()
    {
        fields = new ArrayList<EntityFieldValue>();
        childEntityRelationKeys = new ArrayList<ITypeFieldValueList>();
    }

    @Override
    public Collection<EntityFieldValue> getFields()
    {
        return fields;
    }

    @Override
    public Collection<ITypeFieldValueList> getChildEntityKeys()
    {
        return childEntityRelationKeys;
    }

    @Override
    public EntityFieldValue getFieldValue(String attributeName)
    {
        for (EntityFieldValue fieldValue : fields)
        {
            if (fieldValue.getAttributeName().equals(attributeName))
            {
                return fieldValue;
            }
        }
        return null;
    }

    @Override
    public boolean isValid()
    {
        return fields.size() > 0
                || childEntityRelationKeys.size() > 0;
    }
}
