package dbgate.ermanagement.support.persistant.columntest;

import dbgate.DBColumnType;
import dbgate.DateWrapper;
import dbgate.TimeStampWrapper;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;
import dbgate.ermanagement.DefaultServerDBClass;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 11, 2010
 * Time: 9:45:24 PM
 * To change this template use File | Settings | File Templates.
 */
@DBTableInfo(tableName = "column_test_entity")
public class ColumnTestEntityAnnotations extends DefaultServerDBClass implements IColumnTestEntity
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true,readFromSequence = true,sequenceGeneratorClassName = "dbgate.ermanagement.support.persistant.columntest.PrimaryKeyGenerator")
    private int idCol;
    @DBColumnInfo(columnType = DBColumnType.LONG)
    private long longNotNull;
    @DBColumnInfo(columnType = DBColumnType.LONG,nullable = true)
    private Long longNull;
    @DBColumnInfo(columnType = DBColumnType.BOOLEAN)
    private boolean booleanNotNull;
    @DBColumnInfo(columnType = DBColumnType.BOOLEAN,nullable = true)
    private Boolean booleanNull;
    @DBColumnInfo(columnType = DBColumnType.CHAR)
    private char charNotNull;
    @DBColumnInfo(columnType = DBColumnType.CHAR,nullable = true)
    private Character charNull;
    @DBColumnInfo(columnType = DBColumnType.INTEGER)
    private int intNotNull;
    @DBColumnInfo(columnType = DBColumnType.INTEGER,nullable = true)
    private Integer intNull;
    @DBColumnInfo(columnType = DBColumnType.DATE)
    private DateWrapper dateNotNull;
    @DBColumnInfo(columnType = DBColumnType.DATE,nullable = true)
    private DateWrapper dateNull;
    @DBColumnInfo(columnType = DBColumnType.DOUBLE)
    private double doubleNotNull;
    @DBColumnInfo(columnType = DBColumnType.DOUBLE,nullable = true)
    private Double doubleNull;
    @DBColumnInfo(columnType = DBColumnType.FLOAT)
    private float floatNotNull;
    @DBColumnInfo(columnType = DBColumnType.FLOAT,nullable = true)
    private Float floatNull;
    @DBColumnInfo(columnType = DBColumnType.TIMESTAMP)
    private TimeStampWrapper timestampNotNull;
    @DBColumnInfo(columnType = DBColumnType.TIMESTAMP,nullable = true)
    private TimeStampWrapper timestampNull;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String varcharNotNull;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR,nullable = true)
    private String varcharNull;

    public int getIdCol()
    {
        return idCol;
    }

    public void setIdCol(int idCol)
    {
        this.idCol = idCol;
    }

    public long getLongNotNull()
    {
        return longNotNull;
    }

    public void setLongNotNull(long longNotNull)
    {
        this.longNotNull = longNotNull;
    }

    public Long getLongNull()
    {
        return longNull;
    }

    public void setLongNull(Long longNull)
    {
        this.longNull = longNull;
    }

    public boolean isBooleanNotNull()
    {
        return booleanNotNull;
    }

    public void setBooleanNotNull(boolean booleanNotNull)
    {
        this.booleanNotNull = booleanNotNull;
    }

    public Boolean isBooleanNull()
    {
        return booleanNull;
    }

    public void setBooleanNull(Boolean booleanNull)
    {
        this.booleanNull = booleanNull;
    }

    public char getCharNotNull()
    {
        return charNotNull;
    }

    public void setCharNotNull(char charNotNull)
    {
        this.charNotNull = charNotNull;
    }

    public Character getCharNull()
    {
        return charNull;
    }

    public void setCharNull(Character charNull)
    {
        this.charNull = charNull;
    }

    public int getIntNotNull()
    {
        return intNotNull;
    }

    public void setIntNotNull(int intNotNull)
    {
        this.intNotNull = intNotNull;
    }

    public Integer getIntNull()
    {
        return intNull;
    }

    public void setIntNull(Integer intNull)
    {
        this.intNull = intNull;
    }

    public DateWrapper getDateNotNull()
    {
        return dateNotNull;
    }

    public void setDateNotNull(DateWrapper dateNotNull)
    {
        this.dateNotNull = dateNotNull;
    }

    public DateWrapper getDateNull()
    {
        return dateNull;
    }

    public void setDateNull(DateWrapper dateNull)
    {
        this.dateNull = dateNull;
    }

    public double getDoubleNotNull()
    {
        return doubleNotNull;
    }

    public void setDoubleNotNull(double doubleNotNull)
    {
        this.doubleNotNull = doubleNotNull;
    }

    public Double getDoubleNull()
    {
        return doubleNull;
    }

    public void setDoubleNull(Double doubleNull)
    {
        this.doubleNull = doubleNull;
    }

    public float getFloatNotNull()
    {
        return floatNotNull;
    }

    public void setFloatNotNull(float floatNotNull)
    {
        this.floatNotNull = floatNotNull;
    }

    public Float getFloatNull()
    {
        return floatNull;
    }

    public void setFloatNull(Float floatNull)
    {
        this.floatNull = floatNull;
    }

    public TimeStampWrapper getTimestampNotNull()
    {
        return timestampNotNull;
    }

    public void setTimestampNotNull(TimeStampWrapper timestampNotNull)
    {
        this.timestampNotNull = timestampNotNull;
    }

    public TimeStampWrapper getTimestampNull()
    {
        return timestampNull;
    }

    public void setTimestampNull(TimeStampWrapper timestampNull)
    {
        this.timestampNull = timestampNull;
    }

    public String getVarcharNotNull()
    {
        return varcharNotNull;
    }

    public void setVarcharNotNull(String varcharNotNull)
    {
        this.varcharNotNull = varcharNotNull;
    }

    public String getVarcharNull()
    {
        return varcharNull;
    }

    public void setVarcharNull(String varcharNull)
    {
        this.varcharNull = varcharNull;
    }
}