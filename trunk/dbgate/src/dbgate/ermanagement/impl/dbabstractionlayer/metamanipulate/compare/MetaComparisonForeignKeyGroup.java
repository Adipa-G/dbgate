package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.compare;

import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.MetaForeignKey;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 3, 2010
 * Time: 6:27:35 PM
 */
public class MetaComparisonForeignKeyGroup extends AbstractMetaComparisonGroup
{
    public MetaComparisonForeignKeyGroup(IMetaItem existingItem, IMetaItem requiredItem)
    {
        super(existingItem, requiredItem);
    }

    @Override
    public MetaForeignKey getExistingItem()
    {
        return (MetaForeignKey) super.getExistingItem();
    }

    @Override
    public MetaForeignKey getRequiredItem()
    {
        return (MetaForeignKey) super.getRequiredItem();
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