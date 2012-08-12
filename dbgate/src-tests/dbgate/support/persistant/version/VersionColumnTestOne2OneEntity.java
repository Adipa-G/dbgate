package dbgate.support.persistant.version;

import dbgate.*;
import dbgate.ColumnInfo;
import dbgate.TableInfo;
import dbgate.DefaultEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@TableInfo(tableName = "version_test_one2one")
public class VersionColumnTestOne2OneEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VERSION)
    private int version;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
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