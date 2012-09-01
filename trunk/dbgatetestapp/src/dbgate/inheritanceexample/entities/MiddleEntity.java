package dbgate.inheritanceexample.entities;


import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.TableInfo;
import docgenerate.WikiCodeBlock;

/**
 * Date: Mar 30, 2011
 * Time: 12:09:13 AM
 */
@WikiCodeBlock(id = "inheritance_example_middle_entity")
@TableInfo(tableName = "middle_entity")
public class MiddleEntity extends TopEntity
{
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String middleName;

    public MiddleEntity()
    {
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }
}
