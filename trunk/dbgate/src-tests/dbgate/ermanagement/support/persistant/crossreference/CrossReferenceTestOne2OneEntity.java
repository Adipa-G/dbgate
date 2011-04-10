package dbgate.ermanagement.support.persistant.crossreference;

import dbgate.DBColumnType;
import dbgate.ermanagement.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
@DBTableInfo(tableName = "cross_reference_test_one2one")
public class CrossReferenceTestOne2OneEntity extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int idCol;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfo(name = "fk_one2oneent2root"
            ,relatedObjectType = CrossReferenceTestRootEntity.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
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