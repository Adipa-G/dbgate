package dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare;

import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.MetaItemType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 3, 2010
 * Time: 6:25:31 PM
 */
public abstract class AbstractMetaComparisonGroup implements IMetaComparisonGroup
{
    protected IMetaItem existingItem;
    protected IMetaItem requiredItem;

    public AbstractMetaComparisonGroup(IMetaItem existingItem, IMetaItem requiredItem)
    {
        this.existingItem = existingItem;
        this.requiredItem = requiredItem;
    }

    @Override
    public MetaItemType getItemType()
    {
        if (existingItem != null)
        {
            return existingItem.getItemType();
        }
        else if (requiredItem != null )
        {
            return requiredItem.getItemType();
        }
        return null;
    }

    @Override
    public IMetaItem getExistingItem()
    {
        return existingItem;
    }

    @Override
    public void setExistingItem(IMetaItem existingItem)
    {
        this.existingItem = existingItem;
    }

    @Override
    public IMetaItem getRequiredItem()
    {
        return requiredItem;
    }

    @Override
    public void setRequiredItem(IMetaItem requiredItem)
    {
        this.requiredItem = requiredItem;
    }
}
