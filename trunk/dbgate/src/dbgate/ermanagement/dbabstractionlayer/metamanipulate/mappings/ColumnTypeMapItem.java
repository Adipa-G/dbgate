package dbgate.ermanagement.dbabstractionlayer.metamanipulate.mappings;

import dbgate.ColumnType;

import java.sql.Types;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 4:04:16 PM
 */
public class ColumnTypeMapItem
{
    private String name;
    private ColumnType columnType;
    private String defaultNonNullValue;

    public ColumnTypeMapItem(String name, ColumnType id)
    {
        this.name = name;
        columnType = id;
    }

    public ColumnTypeMapItem(String name, ColumnType columnType, String defaultNonNullValue)
    {
        this(name,columnType);
        this.defaultNonNullValue = defaultNonNullValue;
    }

    public String getName()
    {
        return name;
    }

    public ColumnType getColumnType()
    {
        return columnType;
    }

    public String getDefaultNonNullValue()
    {
        return defaultNonNullValue;
    }
}
