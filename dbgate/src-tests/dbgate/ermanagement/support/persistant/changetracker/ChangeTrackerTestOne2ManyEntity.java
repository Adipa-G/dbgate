package dbgate.ermanagement.support.persistant.changetracker;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;
import dbgate.ermanagement.DefaultServerDBClass;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@DBTableInfo(tableName = "change_tracker_test_one2many")
public class ChangeTrackerTestOne2ManyEntity extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int idCol;
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int indexNo;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;

    public ChangeTrackerTestOne2ManyEntity()
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
