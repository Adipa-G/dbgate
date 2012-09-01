package dbgate.inheritanceexample.entities;


import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.DefaultEntity;
import dbgate.TableInfo;
import docgenerate.WikiCodeBlock;

/**
 * Date: Mar 30, 2011
 * Time: 12:09:13 AM
 */
@WikiCodeBlock(id = "inheritance_example_top_entity")
@TableInfo(tableName = "top_entity")
public class TopEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true,subClassCommonColumn = true)
    private int id;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String superName;

    public TopEntity()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getSuperName()
    {
        return superName;
    }

    public void setSuperName(String superName)
    {
        this.superName = superName;
    }
}
