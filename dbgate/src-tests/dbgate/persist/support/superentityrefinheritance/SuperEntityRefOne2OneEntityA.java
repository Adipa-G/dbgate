package dbgate.persist.support.superentityrefinheritance;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.TableInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@TableInfo(tableName = "super_entity_ref_test_one2one_a")
public class SuperEntityRefOne2OneEntityA extends SuperEntityRefOne2OneEntity
{
    @ColumnInfo(columnType = ColumnType.VARCHAR,size = 100)
    private String nameA;

    public SuperEntityRefOne2OneEntityA()
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