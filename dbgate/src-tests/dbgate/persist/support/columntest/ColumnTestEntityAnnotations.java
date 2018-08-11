package dbgate.persist.support.columntest;

import dbgate.*;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 11, 2010
 * Time: 9:45:24 PM
 * To change this template use File | Settings | File Templates.
 */
@TableInfo(tableName = "column_test_entity")
public class ColumnTestEntityAnnotations extends DefaultEntity implements IColumnTestEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true,readFromSequence = true,sequenceGeneratorClassName = "dbgate.persist.support.columntest.PrimaryKeyGenerator")
    private int idCol;
    @ColumnInfo(columnType = ColumnType.LONG)
    private long longNotNull;
    @ColumnInfo(columnType = ColumnType.LONG,nullable = true)
    private Long longNull;
    @ColumnInfo(columnType = ColumnType.BOOLEAN)
    private boolean booleanNotNull;
    @ColumnInfo(columnType = ColumnType.BOOLEAN,nullable = true)
    private Boolean booleanNull;
    @ColumnInfo(columnType = ColumnType.CHAR)
    private char charNotNull;
    @ColumnInfo(columnType = ColumnType.CHAR,nullable = true)
    private Character charNull;
    @ColumnInfo(columnType = ColumnType.INTEGER)
    private int intNotNull;
    @ColumnInfo(columnType = ColumnType.INTEGER,nullable = true)
    private Integer intNull;
    @ColumnInfo(columnType = ColumnType.DATE)
    private DateWrapper dateNotNull;
    @ColumnInfo(columnType = ColumnType.DATE,nullable = true)
    private DateWrapper dateNull;
    @ColumnInfo(columnType = ColumnType.DOUBLE)
    private double doubleNotNull;
    @ColumnInfo(columnType = ColumnType.DOUBLE,nullable = true)
    private Double doubleNull;
    @ColumnInfo(columnType = ColumnType.FLOAT)
    private float floatNotNull;
    @ColumnInfo(columnType = ColumnType.FLOAT,nullable = true)
    private Float floatNull;
    @ColumnInfo(columnType = ColumnType.TIMESTAMP)
    private TimeStampWrapper timestampNotNull;
    @ColumnInfo(columnType = ColumnType.TIMESTAMP,nullable = true)
    private TimeStampWrapper timestampNull;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String varcharNotNull;
    @ColumnInfo(columnType = ColumnType.VARCHAR,nullable = true)
    private String varcharNull;
    @ColumnInfo(columnType = ColumnType.GUID)
    private UUID guidNotNull;
    @ColumnInfo(columnType = ColumnType.GUID,nullable = true)
    private UUID guidNull;

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

    public UUID getGuidNotNull()
    {
        return guidNotNull;
    }

    public void setGuidNotNull(UUID guidNotNull)
    {
        this.guidNotNull = guidNotNull;
    }

    public UUID getGuidNull()
    {
        return guidNull;
    }

    public void setGuidNull(UUID guidNull)
    {
        this.guidNull = guidNull;
    }
}