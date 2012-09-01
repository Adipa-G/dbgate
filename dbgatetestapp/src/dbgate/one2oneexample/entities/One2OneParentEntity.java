package dbgate.one2oneexample.entities;

import dbgate.*;
import docgenerate.WikiCodeBlock;

/**
 * Date: Mar 30, 2011
 * Time: 8:46:20 PM
 */
@WikiCodeBlock(id = "one_2_one_example_parent_entity")
@TableInfo(tableName = "parent_entity")
public class One2OneParentEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int id;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfoList(infoList = {
        @ForeignKeyInfo(name = "parent2childA"
                    ,relatedObjectType = One2OneChildEntityA.class
                    ,updateRule = ReferentialRuleType.RESTRICT
                    ,deleteRule = ReferentialRuleType.CASCADE
                    ,columnMappings =  {@ForeignKeyColumnMapping(fromField = "id",toField = "parentId")})
        ,
        @ForeignKeyInfo(name = "parent2childB"
                    ,relatedObjectType = One2OneChildEntityB.class
                    ,updateRule = ReferentialRuleType.RESTRICT
                    ,deleteRule = ReferentialRuleType.CASCADE
                    ,columnMappings =  {@ForeignKeyColumnMapping(fromField = "id",toField = "parentId")})
    })
    private One2OneChildEntity childEntity;

    public One2OneParentEntity()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public One2OneChildEntity getChildEntity()
    {
        return childEntity;
    }

    public void setChildEntity(One2OneChildEntity one2OneChildEntity)
    {
        this.childEntity = one2OneChildEntity;
    }
}
