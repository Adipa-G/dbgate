package dbgate.support.persistant.superentityrefinheritance;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.TableInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@TableInfo(tableName = "super_entity_ref_test_one2many_a")
public class SuperEntityRefOne2ManyEntityA extends SuperEntityRefOne2ManyEntity
{
    @ColumnInfo(columnType = ColumnType.VARCHAR,size = 100)
    private String nameA;

    public SuperEntityRefOne2ManyEntityA()
    {
    }

    public String getNameA()
    {
        return nameA;
    }

    public void setNameA(String nameA)
    {
        this.nameA = nameA;
    }
}