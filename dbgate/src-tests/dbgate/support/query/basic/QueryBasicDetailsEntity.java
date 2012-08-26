package dbgate.support.query.basic;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.DefaultEntity;
import dbgate.TableInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "query_basic_details")
public class QueryBasicDetailsEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
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