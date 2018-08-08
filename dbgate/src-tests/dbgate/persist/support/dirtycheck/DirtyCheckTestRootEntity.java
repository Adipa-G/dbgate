package dbgate.persist.support.dirtycheck;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "dirty_check_test_root")
public class DirtyCheckTestRootEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;

    @ForeignKeyInfo(name = "fk_root2one2manyent",
            relatedObjectType = DirtyCheckTestOne2ManyEntity.class,
            fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")},
            updateRule = ReferentialRuleType.RESTRICT,
            deleteRule = ReferentialRuleType.CASCADE)
    private Collection<DirtyCheckTestOne2ManyEntity> one2ManyEntities;

    @ForeignKeyInfo(name = "fk_root2one2oneent",
            relatedObjectType = DirtyCheckTestOne2OneEntity.class,
            fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")},
            updateRule = ReferentialRuleType.RESTRICT,
            deleteRule = ReferentialRuleType.CASCADE)
    private DirtyCheckTestOne2OneEntity one2OneEntity;

    public DirtyCheckTestRootEntity()
    {
        one2ManyEntities = new ArrayList<DirtyCheckTestOne2ManyEntity>();
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

    public Collection<DirtyCheckTestOne2ManyEntity> getOne2ManyEntities()
    {
        return one2ManyEntities;
    }

    public void setOne2ManyEntities(Collection<DirtyCheckTestOne2ManyEntity> one2ManyEntities)
    {
        this.one2ManyEntities = one2ManyEntities;
    }

    public DirtyCheckTestOne2OneEntity getOne2OneEntity()
    {
        return one2OneEntity;
    }

    public void setOne2OneEntity(DirtyCheckTestOne2OneEntity one2OneEntity)
    {
        this.one2OneEntity = one2OneEntity;
    }
}
