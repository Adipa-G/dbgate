package dbgate.inheritanceexample.entities;


import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.TableInfo;

/**
 * Date: Mar 30, 2011
 * Time: 12:09:13 AM
 */
@TableInfo(tableName = "bottom_entity")
public class BottomEntity extends MiddleEntity
{
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String subName;

    public BottomEntity()
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
