package dbgate.ermanagement.context;

import dbgate.ermanagement.IDBColumn;

/**
 * Date: Mar 23, 2011
 * Time: 9:28:40 PM
 */
public class EntityFieldValue
{
    private Object value;
    private IDBColumn dbColumn;

    public EntityFieldValue()
    {
    }

    public EntityFieldValue(Object value, IDBColumn dbColumn)
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

    public IDBColumn getDbColumn()
    {
        return dbColumn;
    }

    public void setDbColumn(IDBColumn dbColumn)
    {
        this.dbColumn = dbColumn;
    }
}
