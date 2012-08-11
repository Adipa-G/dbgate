package dbgate.ermanagement.support.query.basic;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;
import dbgate.ermanagement.DefaultServerDBClass;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@DBTableInfo(tableName = "query_basic_join")
public class QueryBasicJoinEntity extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int idCol;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR,key = true)
    private String name;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
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