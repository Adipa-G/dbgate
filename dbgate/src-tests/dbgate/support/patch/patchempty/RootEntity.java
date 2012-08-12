package dbgate.support.patch.patchempty;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 11, 2010
 * Time: 9:45:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class RootEntity extends AbstractManagedEntity
{
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
    @ForeignKeyInfoList(infoList =
    {
        @ForeignKeyInfo(name = "fk_root2leafa"
            ,relatedObjectType = LeafEntitySubA.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
        ),
        @ForeignKeyInfo(name = "fk_root2leafb"
            ,relatedObjectType = LeafEntitySubB.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
        )
    })
    private Collection<LeafEntity> leafEntities;

    public RootEntity()
    {
        leafEntities = new ArrayList<LeafEntity>();
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

    public Collection<LeafEntity> getLeafEntities()
    {
        return leafEntities;
    }

    public void setLeafEntities(Collection<LeafEntity> leafEntities)
    {
        this.leafEntities = leafEntities;
    }

    public Map<Class,String> getTableNames()
    {
        Map<Class,String> map = new HashMap<Class, String>();
        map.put(this.getClass(),"root_entity");
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = new HashMap<Class, Collection<IField>>();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultColumn("idCol",true, ColumnType.INTEGER));
        dbColumns.add(new DefaultColumn("longNotNull", ColumnType.LONG));
        dbColumns.add(new DefaultColumn("longNull", ColumnType.LONG,true));
        dbColumns.add(new DefaultColumn("booleanNotNull", ColumnType.BOOLEAN));
        dbColumns.add(new DefaultColumn("booleanNull", ColumnType.BOOLEAN,true));
        dbColumns.add(new DefaultColumn("charNotNull", ColumnType.CHAR));
        dbColumns.add(new DefaultColumn("charNull", ColumnType.CHAR,true));
        dbColumns.add(new DefaultColumn("intNotNull", ColumnType.INTEGER));
        dbColumns.add(new DefaultColumn("intNull", ColumnType.INTEGER,true));
        dbColumns.add(new DefaultColumn("dateNotNull", ColumnType.DATE));
        dbColumns.add(new DefaultColumn("dateNull", ColumnType.DATE,true));
        dbColumns.add(new DefaultColumn("doubleNotNull", ColumnType.DOUBLE));
        dbColumns.add(new DefaultColumn("doubleNull", ColumnType.DOUBLE,true));
        dbColumns.add(new DefaultColumn("floatNotNull", ColumnType.FLOAT));
        dbColumns.add(new DefaultColumn("floatNull", ColumnType.FLOAT,true));
        dbColumns.add(new DefaultColumn("timestampNotNull", ColumnType.TIMESTAMP));
        dbColumns.add(new DefaultColumn("timestampNull", ColumnType.TIMESTAMP,true));
        dbColumns.add(new DefaultColumn("varcharNotNull", ColumnType.VARCHAR));
        dbColumns.add(new DefaultColumn("varcharNull", ColumnType.VARCHAR,true));

        map.put(this.getClass(),dbColumns);
        return map;
    }
}