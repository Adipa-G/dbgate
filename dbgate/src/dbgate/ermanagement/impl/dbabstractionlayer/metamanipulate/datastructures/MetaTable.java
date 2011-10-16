package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 3:17:27 PM
 */
public class MetaTable extends AbstractMetaItem
{
    private Collection<MetaColumn> columns;
    private Collection<MetaForeignKey> foreignKeys;
    private MetaPrimaryKey primaryKey;

    public MetaTable()
    {
        itemType = MetaItemType.TABLE;
        columns = new ArrayList<MetaColumn>();
        foreignKeys = new ArrayList<MetaForeignKey>();
    }

    public MetaPrimaryKey getPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(MetaPrimaryKey primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public Collection<MetaColumn> getColumns()
    {
        return columns;
    }

    public void setColumns(Collection<MetaColumn> columns)
    {
        this.columns = columns;
    }

    public Collection<MetaForeignKey> getForeignKeys()
    {
        return foreignKeys;
    }

    public void setForeignKeys(Collection<MetaForeignKey> foreignKeys)
    {
        this.foreignKeys = foreignKeys;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MetaTable that = (MetaTable) o;

        if (primaryKey == null && that.getPrimaryKey() != null) return false;
        if (primaryKey != null && that.getPrimaryKey() == null) return false;
        if (primaryKey != null && that.getPrimaryKey() != null
                && !primaryKey.equals(that.getPrimaryKey())) return false;

        for (MetaForeignKey foreignKey : foreignKeys)
        {
            boolean found = false;
            for (MetaForeignKey thatForeignKey : that.getForeignKeys())
            {
                if (foreignKey.getName().equals(thatForeignKey.getName()))
                {
                    found = true;
                    if (!foreignKey.equals(thatForeignKey)) return false;
                }
            }
            if (!found) return false;
        }

        for (MetaForeignKey thatForeignKey : that.getForeignKeys())
        {
            boolean found = false;
            for (MetaForeignKey foreignKey : foreignKeys)
            {
                if (foreignKey.getName().equals(thatForeignKey.getName()))
                {
                    found = true;
                }
            }
            if (!found) return false;
        }

        for (MetaColumn column : columns)
        {
            boolean found = false;
            for (MetaColumn thatColumn : that.getColumns())
            {
                if (column.getName().equals(thatColumn.getName()))
                {
                    found = true;
                    if (!column.equals(thatColumn)) return false;
                }
            }
            if (!found) return false;
        }

        for (MetaColumn thatColumn : that.getColumns())
        {
            boolean found = false;
            for (MetaColumn column : columns)
            {
                if (column.getName().equals(thatColumn.getName()))
                {
                    found = true;
                }
            }
            if (!found) return false;
        }

        return true;
    }
}
