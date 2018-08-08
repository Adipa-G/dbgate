package dbgate.persist.support.crossreference;

import dbgate.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@TableInfo(tableName = "cross_reference_test_one2many")
public class CrossReferenceTestOne2ManyEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int indexNo;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfo(name = "fk_one2manyent2root",
            relatedObjectType = CrossReferenceTestRootEntity.class,
            fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")},
            updateRule = ReferentialRuleType.RESTRICT,
            deleteRule = ReferentialRuleType.CASCADE)
    private CrossReferenceTestRootEntity rootEntity;

    public CrossReferenceTestOne2ManyEntity()
    {
    }

    public int getIdCol()
    {
        return idCol;
    }

    public void setIdCol(int idCol)
    {
        this.idCol = idCol;
    }

    public int getIndexNo()
    {
        return indexNo;
    }

    public void setIndexNo(int indexNo)
    {
        this.indexNo = indexNo;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public CrossReferenceTestRootEntity getRootEntity()
    {
        return rootEntity;
    }

    public void setRootEntity(CrossReferenceTestRootEntity rootEntity)
    {
        this.rootEntity = rootEntity;
    }
}