package dbgate.persist.support.crossreference;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "cross_reference_test_root")
public class CrossReferenceTestRootEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;

    @ForeignKeyInfo(name = "fk_root2one2manyent",
            relatedObjectType = CrossReferenceTestOne2ManyEntity.class,
            fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")},
            updateRule = ReferentialRuleType.RESTRICT,
            deleteRule = ReferentialRuleType.CASCADE)
    private Collection<CrossReferenceTestOne2ManyEntity> one2ManyEntities;

    @ForeignKeyInfo(name = "fk_root2one2oneent",
            relatedObjectType = CrossReferenceTestOne2OneEntity.class,
            fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")},
            updateRule = ReferentialRuleType.RESTRICT,
            deleteRule = ReferentialRuleType.CASCADE)
    private CrossReferenceTestOne2OneEntity one2OneEntity;

    public CrossReferenceTestRootEntity()
    {
        one2ManyEntities = new ArrayList<CrossReferenceTestOne2ManyEntity>();
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

    public Collection<CrossReferenceTestOne2ManyEntity> getOne2ManyEntities()
    {
        return one2ManyEntities;
    }

    public void setOne2ManyEntities(Collection<CrossReferenceTestOne2ManyEntity> one2ManyEntities)
    {
        this.one2ManyEntities = one2ManyEntities;
    }

    public CrossReferenceTestOne2OneEntity getOne2OneEntity()
    {
        return one2OneEntity;
    }

    public void setOne2OneEntity(CrossReferenceTestOne2OneEntity one2OneEntity)
    {
        this.one2OneEntity = one2OneEntity;
    }
}