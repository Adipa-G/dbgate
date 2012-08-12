package dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare;

import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.MetaPrimaryKey;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 3, 2010
 * Time: 6:27:35 PM
 */
public class MetaComparisonPrimaryKeyGroup extends AbstractMetaComparisonGroup
{
    public MetaComparisonPrimaryKeyGroup(IMetaItem existingItem, IMetaItem requiredItem)
    {
        super(existingItem, requiredItem);
    }

    @Override
    public MetaPrimaryKey getExistingItem()
    {
        return (MetaPrimaryKey) super.getExistingItem();
    }

    @Override
    public MetaPrimaryKey getRequiredItem()
    {
        return (MetaPrimaryKey) super.getRequiredItem();
    }

    @Override
    public boolean _shouldCreateInDB()
    {
        return (requiredItem != null && existingItem == null)
                || (requiredItem != null && !requiredItem.equals(existingItem) );
    }

    @Override
    public boolean _shouldDeleteFromDB()
    {
        return (existingItem != null && requiredItem == null)
                || (existingItem != null && !existingItem.equals(requiredItem) );
    }

    @Override
    public boolean _shouldAlterInDB()
    {
        return false;
    }
}