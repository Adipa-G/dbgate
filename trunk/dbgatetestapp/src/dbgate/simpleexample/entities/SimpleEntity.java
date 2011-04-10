package dbgate.simpleexample.entities;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;
import dbgate.ermanagement.DefaultServerDBClass;

/**
 * Date: Mar 30, 2011
 * Time: 12:09:13 AM
 */
@DBTableInfo(tableName = "simple_entity")
public class SimpleEntity  extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int id;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;

    public SimpleEntity()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
