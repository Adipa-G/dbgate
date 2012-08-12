package dbgate.support.persistant.treetest;

import dbgate.*;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "tree_test_root")
public class TreeTestRootEntityAnnotations extends DefaultEntity implements ITreeTestRootEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;

    @ForeignKeyInfo(name = "fk_root2one2manyent"
            ,relatedObjectType = TreeTestOne2ManyEntityAnnotations.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE)
    private Collection<ITreeTestOne2ManyEntity> one2ManyEntities;

    @ForeignKeyInfo(name = "fk_root2one2oneent"
            ,relatedObjectType = TreeTestOne2OneEntityAnnotations.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE)
    private ITreeTestOne2OneEntity one2OneEntity;

    public TreeTestRootEntityAnnotations()
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

    public Collection<ITreeTestOne2ManyEntity> getOne2ManyEntities()
    {
        return one2ManyEntities;
    }

    public void setOne2ManyEntities(Collection<ITreeTestOne2ManyEntity> one2ManyEntities)
    {
        this.one2ManyEntities = one2ManyEntities;
    }

    public ITreeTestOne2OneEntity getOne2OneEntity()
    {
        return one2OneEntity;
    }

    public void setOne2OneEntity(ITreeTestOne2OneEntity one2OneEntity)
    {
        this.one2OneEntity = one2OneEntity;
    }
}
