package dbgate.inheritanceexample.entities;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;

/**
 * Date: Mar 30, 2011
 * Time: 12:09:13 AM
 */
@DBTableInfo(tableName = "middle_entity")
public class MiddleEntity extends SuperEntity
{
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
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
