package dbgate.inheritanceexample.entities;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;

/**
 * Date: Mar 30, 2011
 * Time: 12:09:13 AM
 */
@DBTableInfo(tableName = "sub_entity")
public class SubEntity extends MiddleEntity
{
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String subName;

    public SubEntity()
    {
    }

    public String getSubName()
    {
        return subName;
    }

    public void setSubName(String subName)
    {
        this.subName = subName;
    }
}
