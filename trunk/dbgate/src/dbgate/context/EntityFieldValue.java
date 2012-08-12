package dbgate.context;

import dbgate.IColumn;

/**
 * Date: Mar 23, 2011
 * Time: 9:28:40 PM
 */
public class EntityFieldValue
{
    private Object value;
    private IColumn dbColumn;

    public EntityFieldValue()
    {
    }

    public EntityFieldValue(Object value, IColumn dbColumn)
    {
        this.value = value;
        this.dbColumn = dbColumn;
    }

    public String getAttributeName()
    {
        return dbColumn.getAttributeName();
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public IColumn getDbColumn()
    {
        return dbColumn;
    }

    public void setDbColumn(IColumn dbColumn)
    {
        this.dbColumn = dbColumn;
    }
}
