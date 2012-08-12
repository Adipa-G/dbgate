package dbgate.support.persistant.changetracker;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "change_tracker_test_root")
public class ChangeTrackerTestRootEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;

    @ForeignKeyInfo(name = "fk_root2one2manyent"
            ,relatedObjectType = ChangeTrackerTestOne2ManyEntity.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE)
    private Collection<ChangeTrackerTestOne2ManyEntity> one2ManyEntities;

    @ForeignKeyInfo(name = "fk_root2one2oneent"
            ,relatedObjectType = ChangeTrackerTestOne2OneEntity.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE)
    private ChangeTrackerTestOne2OneEntity one2OneEntity;

    public ChangeTrackerTestRootEntity()
    {
        one2ManyEntities = new ArrayList<ChangeTrackerTestOne2ManyEntity>();
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

    public Collection<ChangeTrackerTestOne2ManyEntity> getOne2ManyEntities()
    {
        return one2ManyEntities;
    }

    public void setOne2ManyEntities(Collection<ChangeTrackerTestOne2ManyEntity> one2ManyEntities)
    {
        this.one2ManyEntities = one2ManyEntities;
    }

    public ChangeTrackerTestOne2OneEntity getOne2OneEntity()
    {
        return one2OneEntity;
    }

    public void setOne2OneEntity(ChangeTrackerTestOne2OneEntity one2OneEntity)
    {
        this.one2OneEntity = one2OneEntity;
    }
}
