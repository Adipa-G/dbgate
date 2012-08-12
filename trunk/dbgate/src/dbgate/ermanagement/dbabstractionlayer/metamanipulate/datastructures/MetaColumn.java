package dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures;

import dbgate.ColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 3:17:37 PM
 */
public class MetaColumn extends AbstractMetaItem
{
    private ColumnType columnType;
    private Integer size;
    private boolean isNull;

    public MetaColumn()
    {
        itemType = MetaItemType.COLUMN;   
    }

    public ColumnType getColumnType()
    {
        return columnType;
    }

    public void setColumnType(ColumnType columnType)
    {
        this.columnType = columnType;
    }

    public Integer getSize()
    {
        return size;
    }

    public void setSize(Integer size)
    {
        this.size = size;
    }

    public boolean isNull()
    {
        return isNull;
    }

    public void setNull(boolean aNull)
    {
        isNull = aNull;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MetaColumn that = (MetaColumn) o;

        if (isNull != that.isNull) return false;
        if (columnType != that.columnType) return false;
        if ((columnType == ColumnType.VARCHAR || columnType == ColumnType.CHAR)
                && !size.equals(that.size)) return false;

        return true;
    }
}
