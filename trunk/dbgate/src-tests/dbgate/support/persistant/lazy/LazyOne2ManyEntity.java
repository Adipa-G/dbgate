package dbgate.support.persistant.lazy;

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
@TableInfo(tableName = "lazy_test_one2many")
public class LazyOne2ManyEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int indexNo;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;

    public LazyOne2ManyEntity()
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