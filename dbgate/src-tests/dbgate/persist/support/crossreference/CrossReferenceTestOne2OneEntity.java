package dbgate.persist.support.crossreference;

import dbgate.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@TableInfo(tableName = "cross_reference_test_one2one")
public class CrossReferenceTestOne2OneEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfo(name = "fk_one2oneent2root"
            ,relatedObjectType = CrossReferenceTestRootEntity.class
            , fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE)
    private CrossReferenceTestRootEntity rootEntity;

    public CrossReferenceTestOne2OneEntity()
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