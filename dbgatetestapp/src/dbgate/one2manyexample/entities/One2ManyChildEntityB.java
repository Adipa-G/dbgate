package dbgate.one2manyexample.entities;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;
import dbgate.one2oneexample.entities.One2OneChildEntity;

/**
 * Date: Mar 30, 2011
 * Time: 8:47:46 PM
 */
@DBTableInfo(tableName = "child_entity_b")
public class One2ManyChildEntityB extends One2ManyChildEntity
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int parentId;
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int indexNo;
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

    public int getIndexNo()
    {
        return indexNo;
    }

    public void setIndexNo(int indexNo)
    {
        this.indexNo = indexNo;
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
