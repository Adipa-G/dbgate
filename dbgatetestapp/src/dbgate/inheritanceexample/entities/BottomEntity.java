package dbgate.inheritanceexample.entities;


import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.TableInfo;
import docgenerate.WikiCodeBlock;

/**
 * Date: Mar 30, 2011
 * Time: 12:09:13 AM
 */
@WikiCodeBlock(id = "inheritance_example_bottom_entity")
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
