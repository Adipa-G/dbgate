package dbgate.support.persistant.columntest;

import dbgate.EntityStatus;
import dbgate.DateWrapper;
import dbgate.TimeStampWrapper;
import dbgate.context.IEntityContext;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.ermanagement.impl.DbGate;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 11, 2010
 * Time: 9:45:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ColumnTestEntityExts implements IColumnTestEntity
{
    private EntityStatus status;
    private int idCol;
    private long longNotNull;
    private Long longNull;
    private boolean booleanNotNull;
    private Boolean booleanNull;
    private char charNotNull;
    private Character charNull;
    private int intNotNull;
    private Integer intNull;
    private DateWrapper dateNotNull;
    private DateWrapper dateNull;
    private double doubleNotNull;
    private Double doubleNull;
    private float floatNotNull;
    private Float floatNull;
    private TimeStampWrapper timestampNotNull;
    private TimeStampWrapper timestampNull;
    private String varcharNotNull;
    private String varcharNull;

    public ColumnTestEntityExts()
    {
        status = EntityStatus.NEW;
    }

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

    public EntityStatus getStatus()
    {
        return status;
    }

    public void setStatus(EntityStatus status)
    {
        this.status = status;
    }

    public void persist(Connection con) throws PersistException
    {
        DbGate.getSharedInstance().save(this,con);
    }

    public void retrieve(ResultSet rs, Connection con) throws RetrievalException
    {
        DbGate.getSharedInstance().load(this,rs,con);
    }

    public IEntityContext getContext()
    {
        return null;
    }
}