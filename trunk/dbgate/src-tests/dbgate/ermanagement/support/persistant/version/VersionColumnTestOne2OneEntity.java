package dbgate.ermanagement.support.persistant.version;

import dbgate.DBColumnType;
import dbgate.ermanagement.*;
import dbgate.ermanagement.support.persistant.crossreference.CrossReferenceTestRootEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@DBTableInfo(tableName = "version_test_one2one")
public class VersionColumnTestOne2OneEntity extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int idCol;
    @DBColumnInfo(columnType = DBColumnType.VERSION)
    private int version;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;

    public VersionColumnTestOne2OneEntity()
    {
    }

    public int getIdCol()
    {
        return idCol;
    }

    public void setIdCol(int idCol)
    {
        this.idCol = idCol;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
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