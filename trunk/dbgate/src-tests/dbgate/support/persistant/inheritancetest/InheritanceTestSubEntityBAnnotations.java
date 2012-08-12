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
@DBTableInfo(tableName = "inheritance_test_subb")
public class InheritanceTestSubEntityBAnnotations extends InheritanceTestSuperEntityAnnotations implements IInheritanceTestSubEntityB
{
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
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