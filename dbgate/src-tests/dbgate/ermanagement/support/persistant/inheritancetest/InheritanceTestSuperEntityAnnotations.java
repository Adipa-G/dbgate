package dbgate.ermanagement.support.persistant.inheritancetest;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;
import dbgate.ermanagement.DefaultServerDBClass;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@DBTableInfo(tableName = "inheritance_test_super")
public class InheritanceTestSuperEntityAnnotations extends DefaultServerDBClass implements IInheritanceTestSuperEntity
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true,subClassCommonColumn = true)
    private int idCol;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;

    public InheritanceTestSuperEntityAnnotations()
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