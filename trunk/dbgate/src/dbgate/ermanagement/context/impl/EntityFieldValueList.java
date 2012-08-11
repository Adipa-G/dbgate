package dbgate.ermanagement.context.impl;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.context.IEntityFieldValueList;

/**
 * Date: Mar 22, 2011
 * Time: 9:28:55 PM
 */
public class EntityFieldValueList extends EntityTypeFieldValueList implements IEntityFieldValueList
{
    private ServerRODBClass entity;

    public EntityFieldValueList(ServerRODBClass entity)
    {
        super(entity.getClass());
        this.entity = entity;
    }

    @Override
    public ServerRODBClass getEntity()
    {
        return entity;
    }
}