package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.compare;

import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.MetaItemType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 3, 2010
 * Time: 6:22:26 PM
 */
public interface IMetaComparisonGroup
{
    MetaItemType getItemType();

    IMetaItem getExistingItem();

    void setExistingItem(IMetaItem item);

    IMetaItem getRequiredItem();

    void setRequiredItem(IMetaItem item);

    boolean _shouldCreateInDB();

    boolean _shouldDeleteFromDB();

    boolean _shouldAlterInDB();
}
