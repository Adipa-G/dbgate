package dbgate.ermanagement.support.persistant.lazy;

import dbgate.ColumnType;
import dbgate.ermanagement.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "lazy_test_root")
public class LazyRootEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;

    @ForeignKeyInfo(name = "fk_root2one2manyent"
            ,relatedObjectType = LazyOne2ManyEntity.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE
            ,lazy = true)
    private Collection<LazyOne2ManyEntity> one2ManyEntities;

    @ForeignKeyInfo(name = "fk_root2one2oneent"
            ,relatedObjectType = LazyOne2OneEntity.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE
            ,lazy = true)
    private LazyOne2OneEntity one2OneEntity;

    public LazyRootEntity()
    {
        one2ManyEntities = new ArrayList<LazyOne2ManyEntity>();
    }

    public int getIdCol()
    {
        return idCol;
    }

    public void setIdCol(int idCol)
    {
        this.idCol = idCol;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Collection<LazyOne2ManyEntity> getOne2ManyEntities()
    {
        return one2ManyEntities;
    }

    public void setOne2ManyEntities(Collection<LazyOne2ManyEntity> one2ManyEntities)
    {
        this.one2ManyEntities = one2ManyEntities;
    }

    public LazyOne2OneEntity getOne2OneEntity()
    {
        return one2OneEntity;
    }

    public void setOne2OneEntity(LazyOne2OneEntity one2OneEntity)
    {
        this.one2OneEntity = one2OneEntity;
    }
}