package dbgate.support.persistant.inheritancetest;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.TableInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@TableInfo(tableName = "inheritance_test_suba")
public class InheritanceTestSubEntityAAnnotations extends InheritanceTestSuperEntityAnnotations implements IInheritanceTestSubEntityA
{
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String nameA;

    public String getNameA()
    {
        return nameA;
    }

    public void setNameA(String nameA)
    {
        this.nameA = nameA;
    }
}