package dbgate.one2oneexample.entities;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;

/**
 * Date: Mar 30, 2011
 * Time: 8:47:46 PM
 */
@DBTableInfo(tableName = "child_entity_b")
public class One2OneChildEntityB extends One2OneChildEntity
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int parentId;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
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
