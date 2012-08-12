package dbgate.ermanagement.support.persistant.inheritancetest;

import dbgate.ColumnType;
import dbgate.ermanagement.ColumnInfo;
import dbgate.ermanagement.TableInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@TableInfo(tableName = "inheritance_test_subb")
public class InheritanceTestSubEntityBAnnotations extends InheritanceTestSuperEntityAnnotations implements IInheritanceTestSubEntityB
{
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String nameB;

    public String getNameB()
    {
        return nameB;
    }

    public void setNameB(String nameB)
    {
        this.nameB = nameB;
    }
}