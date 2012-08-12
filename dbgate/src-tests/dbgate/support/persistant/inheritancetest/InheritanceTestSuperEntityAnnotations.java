package dbgate.support.persistant.inheritancetest;

import dbgate.*;
import dbgate.ColumnInfo;
import dbgate.TableInfo;
import dbgate.DefaultEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@TableInfo(tableName = "inheritance_test_super")
public class InheritanceTestSuperEntityAnnotations extends DefaultEntity implements IInheritanceTestSuperEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true,subClassCommonColumn = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
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