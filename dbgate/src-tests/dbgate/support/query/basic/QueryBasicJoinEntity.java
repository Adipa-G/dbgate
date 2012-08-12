package dbgate.support.query.basic;

import dbgate.*;
import dbgate.ColumnInfo;
import dbgate.TableInfo;
import dbgate.DefaultEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "query_basic_join")
public class QueryBasicJoinEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR,key = true)
    private String name;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String overrideDescription;

    public QueryBasicJoinEntity()
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

    public String getOverrideDescription()
    {
        return overrideDescription;
    }

    public void setOverrideDescription(String overrideDescription)
    {
        this.overrideDescription = overrideDescription;
    }
}