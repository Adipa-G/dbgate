package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.mappings;

import dbgate.DBColumnType;

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
    private DBColumnType columnType;
    private String defaultNonNullValue;

    public ColumnTypeMapItem()
    {
    }

    public ColumnTypeMapItem(String name, DBColumnType id)
    {
        this.name = name;
        columnType = id;
    }

    public ColumnTypeMapItem(String name, DBColumnType columnType, String defaultNonNullValue)
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

    public DBColumnType getColumnType()
    {
        return columnType;
    }

    public void setColumnType(DBColumnType columnType)
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
                columnType = DBColumnType.LONG;
                break;
//            case Types.BINARY:
//                break;
//            case Types.BIT:
//                columnType = DBColumnType.BOOLEAN;
//                break;
//            case Types.BLOB:
//                break;
//            case Types.TINYINT:
//                columnType = DBColumnType.INTEGER;
//                break;
//            case Types.SMALLINT:
//                columnType = DBColumnType.INTEGER;
//                break;
            case Types.INTEGER:
                columnType = DBColumnType.INTEGER;
                break;
            case Types.FLOAT:
                columnType = DBColumnType.FLOAT;
                break;
//            case Types.REAL:
//                columnType = DBColumnType.FLOAT;
//                break;
            case Types.DOUBLE:
                columnType = DBColumnType.DOUBLE;
                break;
//            case Types.NUMERIC:
//                columnType = DBColumnType.DOUBLE;
//                break;
            case Types.DECIMAL:
                columnType = DBColumnType.DOUBLE;
                break;
            case Types.CHAR:
                columnType = DBColumnType.BOOLEAN;
                break;
            case Types.VARCHAR:
                columnType = DBColumnType.VARCHAR;
                break;
//            case Types.LONGVARCHAR:
//                columnType = DBColumnType.VARCHAR;
//                break;
            case Types.DATE:
                columnType = DBColumnType.DATE;
                break;
//            case Types.TIME:
//                break;
            case Types.TIMESTAMP:
                columnType = DBColumnType.TIMESTAMP;
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
//                columnType = DBColumnType.VARCHAR;
//                break;
//            case Types.REF:
//                break;
//            case Types.DATALINK:
//                break;
            case Types.BOOLEAN:
                columnType = DBColumnType.BOOLEAN;
                break;
//            case Types.ROWID:
//                break;
            case Types.NCHAR:
                columnType = DBColumnType.CHAR;
                break;
//            case Types.NVARCHAR:
//                columnType = DBColumnType.VARCHAR;
//                break;
//            case Types.LONGNVARCHAR:
//                columnType = DBColumnType.VARCHAR;
//                break;
//            case Types.NCLOB:
//                break;
//            case Types.SQLXML:
//                break;
        }
    }
}
