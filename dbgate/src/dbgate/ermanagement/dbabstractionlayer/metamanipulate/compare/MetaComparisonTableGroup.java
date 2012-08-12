package dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare;

import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.MetaTable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 3, 2010
 * Time: 6:27:35 PM
 */
public class MetaComparisonTableGroup extends AbstractMetaComparisonGroup
{
    private Collection<MetaComparisonColumnGroup> columns;
    private Collection<MetaComparisonForeignKeyGroup> foreignKeys;
    private MetaComparisonPrimaryKeyGroup primaryKey;

    public MetaComparisonTableGroup(IMetaItem existingItem, IMetaItem requiredItem)
    {
        super(existingItem, requiredItem);
        columns = new ArrayList<MetaComparisonColumnGroup>();
        foreignKeys = new ArrayList<MetaComparisonForeignKeyGroup>();
    }

    @Override
    public MetaTable getExistingItem()
    {
        return (MetaTable) super.getExistingItem();
    }

    @Override
    public MetaTable getRequiredItem()
    {
        return (MetaTable) super.getRequiredItem();
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

    public Collection<MetaComparisonColumnGroup> getColumns()
    {
        return columns;
    }

    public void setColumns(Collection<MetaComparisonColumnGroup> columns)
    {
        this.columns = columns;
    }

    public Collection<MetaComparisonForeignKeyGroup> getForeignKeys()
    {
        return foreignKeys;
    }

    public void setForeignKeys(Collection<MetaComparisonForeignKeyGroup> foreignKeys)
    {
        this.foreignKeys = foreignKeys;
    }

    public MetaComparisonPrimaryKeyGroup getPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(MetaComparisonPrimaryKeyGroup primaryKey)
    {
        this.primaryKey = primaryKey;
    }
}