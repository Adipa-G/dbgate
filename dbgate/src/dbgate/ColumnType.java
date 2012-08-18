package dbgate;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 29, 2010
 * Time: 12:26:12 PM
 */
public enum ColumnType
{
    LONG,
    BOOLEAN,
    CHAR,
    INTEGER,
    DATE,
    DOUBLE,
    FLOAT,
    TIMESTAMP,
    VARCHAR,
    VERSION;
    
    private static HashMap<Class,ColumnType> javaType2ColumnType;
    private static HashMap<ColumnType,Class> columnType2javaType;
    private static HashMap<ColumnType,Integer> columnType2sqlType;
    
    static 
    {
        synchronized (ColumnType.class)
        {
            javaType2ColumnType = new HashMap<>();
            columnType2javaType = new HashMap<>();
            columnType2sqlType = new HashMap<>();

            addJavaTypeAndColumnTypeRelation(Long.class,LONG);
            addJavaTypeAndColumnTypeRelation(Boolean.class,BOOLEAN);
            addJavaTypeAndColumnTypeRelation(Character.class,CHAR);
            addJavaTypeAndColumnTypeRelation(Integer.class,INTEGER);
            addJavaTypeAndColumnTypeRelation(Date.class,DATE);
            addJavaTypeAndColumnTypeRelation(DateWrapper.class,DATE);
            addJavaTypeAndColumnTypeRelation(Double.class,DOUBLE);
            addJavaTypeAndColumnTypeRelation(Float.class,FLOAT);
            addJavaTypeAndColumnTypeRelation(TimeStampWrapper.class,TIMESTAMP);
            addJavaTypeAndColumnTypeRelation(String.class,VARCHAR);
            addJavaTypeAndColumnTypeRelation(Integer.class,VERSION);

            addSqlTypeAndColumnTypeRelation(Types.BIGINT,LONG);
            addSqlTypeAndColumnTypeRelation(Types.BOOLEAN,BOOLEAN);
            addSqlTypeAndColumnTypeRelation(Types.VARCHAR,CHAR);
            addSqlTypeAndColumnTypeRelation(Types.INTEGER,INTEGER);
            addSqlTypeAndColumnTypeRelation(Types.DATE,DATE);
            addSqlTypeAndColumnTypeRelation(Types.DOUBLE,DOUBLE);
            addSqlTypeAndColumnTypeRelation(Types.FLOAT,FLOAT);
            addSqlTypeAndColumnTypeRelation(Types.TIMESTAMP,TIMESTAMP);
            addSqlTypeAndColumnTypeRelation(Types.VARCHAR,VARCHAR);
            addSqlTypeAndColumnTypeRelation(Types.INTEGER,VERSION);
        }
    }

    private static void addJavaTypeAndColumnTypeRelation(Class type,ColumnType columnType)
    {
        if (!javaType2ColumnType.containsKey(type))
            javaType2ColumnType.put(type,columnType);
        if (!columnType2javaType.containsKey(columnType))
            columnType2javaType.put(columnType,type);
    }

    private static void addSqlTypeAndColumnTypeRelation(int sqlType,ColumnType columnType)
    {
        if (!columnType2sqlType.containsKey(columnType))
            columnType2sqlType.put(columnType, sqlType);
    }

    public static ColumnType getColumnType(Class type)
    {
        return javaType2ColumnType.get(type);
    }

    public static Class getJavaType(ColumnType columnType)
    {
        return columnType2javaType.get(columnType);
    }

    public static int getSqlType(ColumnType columnType)
    {
        return columnType2sqlType.get(columnType);
    }
}
