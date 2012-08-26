package dbgate.context.impl;

import dbgate.context.EntityFieldValue;
import dbgate.context.IChangeTracker;
import dbgate.context.ITypeFieldValueList;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

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
        fields = new CopyOnWriteArrayList<>();
        childEntityRelationKeys = new CopyOnWriteArrayList<>();
    }

    @Override
    public Collection<EntityFieldValue> getFields()
    {
        return Collections.unmodifiableCollection(fields);
    }

    @Override
    public void addFields(Collection<EntityFieldValue> entityFieldValues)
    {
        for (EntityFieldValue field : entityFieldValues)
        {
            EntityFieldValue existing =  getFieldValue(field.getAttributeName());
            if (existing != null)
            {
                existing.setValue(field.getValue());
            }
            else
            {
                this.fields.add(field);
            }
        }
    }

    @Override
    public Collection<ITypeFieldValueList> getChildEntityKeys()
    {
        return Collections.unmodifiableCollection(childEntityRelationKeys);
    }

    @Override
    public void addChildEntityKey(ITypeFieldValueList childEntityKey)
    {
        childEntityRelationKeys.add(childEntityKey);
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
