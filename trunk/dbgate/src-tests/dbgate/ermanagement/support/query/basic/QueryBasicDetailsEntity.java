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
@DBTableInfo(tableName = "query_basic_details")
public class QueryBasicDetailsEntity extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String description;

    public QueryBasicDetailsEntity()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}