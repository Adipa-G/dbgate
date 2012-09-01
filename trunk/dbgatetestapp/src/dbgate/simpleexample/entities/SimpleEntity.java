package dbgate.simpleexample.entities;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.DefaultEntity;
import dbgate.TableInfo;
import docgenerate.WikiCodeBlock;

/**
 * Date: Mar 30, 2011
 * Time: 12:09:13 AM
 */
@WikiCodeBlock(id = "simple_example_simple_entity")
@TableInfo(tableName = "simple_entity")
public class SimpleEntity  extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int id;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
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
