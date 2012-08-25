package dbgate.support.persistant.superentityrefinheritance;

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
@TableInfo(tableName = "super_entity_ref_test_one2many_b")
public class SuperEntityRefOne2ManyEntityB extends SuperEntityRefOne2ManyEntity
{
    @ColumnInfo(columnType = ColumnType.VARCHAR,size = 100)
    private String nameB;

    public SuperEntityRefOne2ManyEntityB()
    {
    }

    public String getNameB()
    {
        return nameB;
    }

    public void setNameB(String nameB)
    {
        this.nameB = nameB;
    }
}