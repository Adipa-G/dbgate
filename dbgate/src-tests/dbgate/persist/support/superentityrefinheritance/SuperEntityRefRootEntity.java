package dbgate.persist.support.superentityrefinheritance;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "super_entity_ref_test_root")
public class SuperEntityRefRootEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR,size = 100)
    private String name;

    @ForeignKeyInfo(name = "fk_root2one2manyent",
            relatedObjectType = SuperEntityRefOne2ManyEntity.class,
            fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")},
            updateRule = ReferentialRuleType.RESTRICT,
            deleteRule = ReferentialRuleType.CASCADE)
    private Collection<SuperEntityRefOne2ManyEntity> one2ManyEntities;

    @ForeignKeyInfo(name = "fk_root2one2oneent",
            relatedObjectType = SuperEntityRefOne2OneEntity.class,
            fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")},
            updateRule = ReferentialRuleType.RESTRICT,
            deleteRule = ReferentialRuleType.CASCADE)
    private SuperEntityRefOne2OneEntity one2OneEntity;

    public SuperEntityRefRootEntity()
    {
        one2ManyEntities = new ArrayList<SuperEntityRefOne2ManyEntity>();
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

    public Collection<SuperEntityRefOne2ManyEntity> getOne2ManyEntities()
    {
        return one2ManyEntities;
    }

    public void setOne2ManyEntities(Collection<SuperEntityRefOne2ManyEntity> one2ManyEntities)
    {
        this.one2ManyEntities = one2ManyEntities;
    }

    public SuperEntityRefOne2OneEntity getOne2OneEntity()
    {
        return one2OneEntity;
    }

    public void setOne2OneEntity(SuperEntityRefOne2OneEntity one2OneEntity)
    {
        this.one2OneEntity = one2OneEntity;
    }
}