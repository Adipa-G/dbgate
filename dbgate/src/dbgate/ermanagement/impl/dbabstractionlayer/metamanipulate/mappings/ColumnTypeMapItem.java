package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.mappings;

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

    public ColumnTypeMapItem()
    {
    }

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

    public void setName(String name)
    {
        this.name = name;
    }

    public ColumnType getColumnType()
    {
        return columnType;
    }

    public void setColumnType(ColumnType columnType)
    {
        this.columnType = columnType;
    }

    public String getDefaultNonNullValue()
    {
        return defaultNonNullValue;
    }

    public void setDefaultNonNullValue(String defaultNonNullValue)
    {
        this.defaultNonNullValue = defaultNonNullValue;
    }

    public void _setTypeFromSqlType(short type)
    {
        switch (type)
        {
//            case Types.ARRAY:
//                break;
            case Types.BIGINT:
                columnType = ColumnType.LONG;
                break;
//            case Types.BINARY:
//                break;
//            case Types.BIT:
//                columnType = ColumnType.BOOLEAN;
//                break;
//            case Types.BLOB:
//                break;
//            case Types.TINYINT:
//                columnType = ColumnType.INTEGER;
//                break;
//            case Types.SMALLINT:
//                columnType = ColumnType.INTEGER;
//                break;
            case Types.INTEGER:
                columnType = ColumnType.INTEGER;
                break;
            case Types.FLOAT:
                columnType = ColumnType.FLOAT;
                break;
//            case Types.REAL:
//                columnType = ColumnType.FLOAT;
//                break;
            case Types.DOUBLE:
                columnType = ColumnType.DOUBLE;
                break;
//            case Types.NUMERIC:
//                columnType = ColumnType.DOUBLE;
//                break;
            case Types.DECIMAL:
                columnType = ColumnType.DOUBLE;
                break;
            case Types.CHAR:
                columnType = ColumnType.BOOLEAN;
                break;
            case Types.VARCHAR:
                columnType = ColumnType.VARCHAR;
                break;
//            case Types.LONGVARCHAR:
//                columnType = ColumnType.VARCHAR;
//                break;
            case Types.DATE:
                columnType = ColumnType.DATE;
                break;
//            case Types.TIME:
//                break;
            case Types.TIMESTAMP:
                columnType = ColumnType.TIMESTAMP;
                break;
//            case Types.VARBINARY:
//                break;
//            case Types.LONGVARBINARY:
//                break;
//            case Types.NULL:
//                break;
//            case Types.OTHER:
//                break;
//            case Types.JAVA_OBJECT:
//                break;
//            case Types.DISTINCT:
//                break;
//            case Types.STRUCT:
//                break;
//            case Types.CLOB:
//                columnType = ColumnType.VARCHAR;
//                break;
//            case Types.REF:
//                break;
//            case Types.DATALINK:
//                break;
            case Types.BOOLEAN:
                columnType = ColumnType.BOOLEAN;
                break;
//            case Types.ROWID:
//                break;
            case Types.NCHAR:
                columnType = ColumnType.CHAR;
                break;
//            case Types.NVARCHAR:
//                columnType = ColumnType.VARCHAR;
//                break;
//            case Types.LONGNVARCHAR:
//                columnType = ColumnType.VARCHAR;
//                break;
//            case Types.NCLOB:
//                break;
//            case Types.SQLXML:
//                break;
        }
    }
}
