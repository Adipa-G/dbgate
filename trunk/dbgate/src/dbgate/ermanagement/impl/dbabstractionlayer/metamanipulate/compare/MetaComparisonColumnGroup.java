package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.compare;

import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.MetaColumn;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 3, 2010
 * Time: 6:27:35 PM
 */
public class MetaComparisonColumnGroup extends AbstractMetaComparisonGroup
{
    public MetaComparisonColumnGroup(IMetaItem existingItem, IMetaItem requiredItem)
    {
        super(existingItem, requiredItem);
    }

    @Override
    public MetaColumn getExistingItem()
    {
        return (MetaColumn) super.getExistingItem();
    }

    @Override
    public MetaColumn getRequiredItem()
    {
        return (MetaColumn) super.getRequiredItem();
    }

    @Override
    public boolean _shouldCreateInDB()
    {
        return requiredItem != null && existingItem == null;
    }

    @Override
    public boolean _shouldDeleteFromDB()
    {
        return existingItem != null && requiredItem == null;
    }

    @Override
    public boolean _shouldAlterInDB()
    {
        return existingItem != null && requiredItem != null && !existingItem.equals(requiredItem);
    }
}
