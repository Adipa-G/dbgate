package dbgate.inheritanceexample.entities;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;
import dbgate.ermanagement.DefaultServerDBClass;

/**
 * Date: Mar 30, 2011
 * Time: 12:09:13 AM
 */
@DBTableInfo(tableName = "super_entity")
public class SuperEntity extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true,subClassCommonColumn = true)
    private int id;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String superName;

    public SuperEntity()
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

    public String getSuperName()
    {
        return superName;
    }

    public void setSuperName(String superName)
    {
        this.superName = superName;
    }
}
