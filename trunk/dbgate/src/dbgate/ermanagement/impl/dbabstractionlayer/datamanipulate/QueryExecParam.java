package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.DBColumnType;

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
    private DBColumnType type;
    private Object value;

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public DBColumnType getType()
    {
        return type;
    }

    public void setType(DBColumnType type)
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
