package dbgate.context.impl;

import dbgate.IReadOnlyEntity;
import dbgate.context.IEntityFieldValueList;

/**
 * Date: Mar 22, 2011
 * Time: 9:28:55 PM
 */
public class EntityFieldValueList extends EntityTypeFieldValueList implements IEntityFieldValueList
{
    private IReadOnlyEntity entity;

    public EntityFieldValueList(IReadOnlyEntity entity)
    {
        super(entity.getClass());
        this.entity = entity;
    }

    @Override
    public IReadOnlyEntity getEntity()
    {
        return entity;
    }
}