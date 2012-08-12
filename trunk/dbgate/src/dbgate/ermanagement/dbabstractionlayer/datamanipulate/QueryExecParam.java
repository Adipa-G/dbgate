package dbgate.ermanagement.dbabstractionlayer.datamanipulate;

import dbgate.ColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryExecParam
{
    private int index;
    private ColumnType type;
    private Object value;

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public ColumnType getType()
    {
        return type;
    }

    public void setType(ColumnType type)
    {
        this.type = type;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }
}
