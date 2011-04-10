package dbgate.ermanagement.support.persistant.crossreference;

import dbgate.DBColumnType;
import dbgate.ermanagement.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@DBTableInfo(tableName = "cross_reference_test_one2many")
public class CrossReferenceTestOne2ManyEntity extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int idCol;
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int indexNo;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfo(name = "fk_one2manyent2root"
            ,relatedObjectType = CrossReferenceTestRootEntity.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE)
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