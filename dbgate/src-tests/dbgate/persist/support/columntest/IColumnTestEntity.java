package dbgate.persist.support.columntest;

import dbgate.DateWrapper;
import dbgate.IEntity;
import dbgate.TimeStampWrapper;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:07:41 AM
 */
public interface IColumnTestEntity extends IEntity
{
    int getIdCol();

    void setIdCol(int idCol);

    long getLongNotNull();

    void setLongNotNull(long longNotNull);

    Long getLongNull();

    void setLongNull(Long longNull);

    boolean isBooleanNotNull();

    void setBooleanNotNull(boolean booleanNotNull);

    Boolean isBooleanNull();

    void setBooleanNull(Boolean booleanNull);

    char getCharNotNull();

    void setCharNotNull(char charNotNull);

    Character getCharNull();

    void setCharNull(Character charNull);

    int getIntNotNull();

    void setIntNotNull(int intNotNull);

    Integer getIntNull();

    void setIntNull(Integer intNull);

    DateWrapper getDateNotNull();

    void setDateNotNull(DateWrapper dateNotNull);

    DateWrapper getDateNull();

    void setDateNull(DateWrapper dateNull);

    double getDoubleNotNull();

    void setDoubleNotNull(double doubleNotNull);

    Double getDoubleNull();

    void setDoubleNull(Double doubleNull);

    float getFloatNotNull();

    void setFloatNotNull(float floatNotNull);

    Float getFloatNull();

    void setFloatNull(Float floatNull);

    TimeStampWrapper getTimestampNotNull();

    void setTimestampNotNull(TimeStampWrapper timestampNotNull);

    TimeStampWrapper getTimestampNull();

    void setTimestampNull(TimeStampWrapper timestampNull);

    String getVarcharNotNull();

    void setVarcharNotNull(String varcharNotNull);

    String getVarcharNull();

    void setVarcharNull(String varcharNull);

    UUID getGuidNotNull();

    void setGuidNotNull(UUID guidNotNull);

    UUID getGuidNull();

    void setGuidNull(UUID guidNull);
}
