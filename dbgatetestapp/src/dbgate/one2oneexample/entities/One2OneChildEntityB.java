package dbgate.one2oneexample.entities;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.TableInfo;

/**
 * Date: Mar 30, 2011
 * Time: 8:47:46 PM
 */
@TableInfo(tableName = "child_entity_b")
public class One2OneChildEntityB extends One2OneChildEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int parentId;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;


    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
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
