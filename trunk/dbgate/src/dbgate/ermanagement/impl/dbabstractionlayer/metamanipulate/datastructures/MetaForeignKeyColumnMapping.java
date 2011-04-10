package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 4:26:03 PM
 */
public class MetaForeignKeyColumnMapping
{
    private String fromColumn;
    private String toColumn;

    public MetaForeignKeyColumnMapping(String fromColumn, String toColumn)
    {
        this.fromColumn = fromColumn;
        this.toColumn = toColumn;
    }

    public String getFromColumn()
    {
        return fromColumn;
    }

    public void setFromColumn(String fromColumn)
    {
        this.fromColumn = fromColumn;
    }

    public String getToColumn()
    {
        return toColumn;
    }

    public void setToColumn(String toColumn)
    {
        this.toColumn = toColumn;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaForeignKeyColumnMapping that = (MetaForeignKeyColumnMapping) o;

        if (fromColumn != null && !fromColumn.equalsIgnoreCase(that.fromColumn))
        {
            return false;
        }
        if (toColumn != null && !toColumn.equalsIgnoreCase(that.toColumn))
        {
            return false;
        }
        return true;
    }
}
