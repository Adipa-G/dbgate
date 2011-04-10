package dbgate.ermanagement.support.persistant.inheritancetest;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@DBTableInfo(tableName = "inheritance_test_suba")
public class InheritanceTestSubEntityAAnnotations extends InheritanceTestSuperEntityAnnotations implements IInheritanceTestSubEntityA
{
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
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