package dbgate.persist.support.superentityrefinheritance;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.DefaultEntity;
import dbgate.TableInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@TableInfo(tableName = "super_entity_ref_test_one2many")
public class SuperEntityRefOne2ManyEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true,subClassCommonColumn = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true,subClassCommonColumn = true)
    private int indexNo;
    @ColumnInfo(columnType = ColumnType.VARCHAR,size = 100)
    private String name;

    public SuperEntityRefOne2ManyEntity()
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