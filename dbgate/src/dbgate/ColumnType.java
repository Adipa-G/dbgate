package dbgate;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 29, 2010
 * Time: 12:26:12 PM
 */
public enum ColumnType
{
    GUID,
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
    private static HashMap<ColumnType,Class> columnType2JavaType;
    private static HashMap<ColumnType,Class> columnType2JavaPrimitiveType;
    private static HashMap<ColumnType,Integer> columnType2sqlType;
    private static HashMap<Integer,ColumnType> sqlType2ColumnType;

    static 
    {
        synchronized (ColumnType.class)
        {
            javaType2ColumnType = new HashMap<>();
            columnType2JavaType = new HashMap<>();
            columnType2JavaPrimitiveType = new HashMap<>();
            columnType2sqlType = new HashMap<>();
            sqlType2ColumnType = new HashMap<>();

            addJavaTypeAndColumnTypeRelation(UUID.class, null, GUID);
            addJavaTypeAndColumnTypeRelation(Long.class,Long.TYPE,LONG);
            addJavaTypeAndColumnTypeRelation(Boolean.class,Boolean.TYPE,BOOLEAN);
            addJavaTypeAndColumnTypeRelation(Character.class,Character.TYPE,CHAR);
            addJavaTypeAndColumnTypeRelation(Integer.class,Integer.TYPE,INTEGER);
            addJavaTypeAndColumnTypeRelation(DateWrapper.class,null,DATE);
            addJavaTypeAndColumnTypeRelation(Double.class,Double.TYPE,DOUBLE);
            addJavaTypeAndColumnTypeRelation(Float.class,Float.TYPE,FLOAT);
            addJavaTypeAndColumnTypeRelation(TimeStampWrapper.class,null,TIMESTAMP);
            addJavaTypeAndColumnTypeRelation(String.class,null,VARCHAR);
            addJavaTypeAndColumnTypeRelation(Integer.class,Integer.TYPE,VERSION);

            addSqlTypeAndColumnTypeRelation(Types.VARCHAR,GUID);
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

    private static void addJavaTypeAndColumnTypeRelation(Class type,Class primitiveType,ColumnType columnType)
    {
        if (!javaType2ColumnType.containsKey(type))
            javaType2ColumnType.put(type,columnType);

        if (primitiveType != null
                && !javaType2ColumnType.containsKey(primitiveType))
            javaType2ColumnType.put(primitiveType,columnType);

        if (!columnType2JavaType.containsKey(columnType))
            columnType2JavaType.put(columnType,type);

        if (primitiveType != null
                && !columnType2JavaPrimitiveType.containsKey(columnType))
            columnType2JavaPrimitiveType.put(columnType,primitiveType);
    }

    private static void addSqlTypeAndColumnTypeRelation(int sqlType,ColumnType columnType)
    {
        if (!columnType2sqlType.containsKey(columnType))
            columnType2sqlType.put(columnType, sqlType);

        if (!sqlType2ColumnType.containsKey(sqlType))
            sqlType2ColumnType.put(sqlType, columnType);
    }

    public static ColumnType getColumnType(Class type)
    {
        return javaType2ColumnType.get(type);
    }

    public static Class getJavaType(ColumnType columnType)
    {
        return columnType2JavaType.get(columnType);
    }

    public static Class getJavaPrimitiveType(ColumnType columnType)
    {
        if (columnType2JavaPrimitiveType.containsKey(columnType))
            return columnType2JavaPrimitiveType.get(columnType);
        return getJavaType(columnType);
    }

    public static ColumnType getColumnType(int sqlType)
    {
        return sqlType2ColumnType.get(sqlType);
    }

    public static int getSqlType(ColumnType columnType)
    {
        return columnType2sqlType.get(columnType);
    }
}
