package dbgate.support.persistant.multidb;

import dbgate.*;
import dbgate.support.persistant.superentityrefinheritance.SuperEntityRefOne2ManyEntity;
import dbgate.support.persistant.superentityrefinheritance.SuperEntityRefOne2OneEntity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "multi_db_test_root")
public class MultiDbEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR,size = 100)
    private String name;

    public MultiDbEntity()
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}