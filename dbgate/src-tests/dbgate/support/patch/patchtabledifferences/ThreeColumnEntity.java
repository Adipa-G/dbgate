package dbgate.support.patch.patchtabledifferences;

import dbgate.*;
import dbgate.ColumnInfo;
import dbgate.TableInfo;
import dbgate.DefaultEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 16, 2011
 * Time: 8:44:23 AM
 */
@TableInfo(tableName = "table_change_test_entity")
public class ThreeColumnEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int indexNo;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;

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
