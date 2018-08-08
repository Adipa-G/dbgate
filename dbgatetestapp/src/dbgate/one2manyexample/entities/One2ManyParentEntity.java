package dbgate.one2manyexample.entities;

import dbgate.*;
import docgenerate.WikiCodeBlock;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 30, 2011
 * Time: 8:46:20 PM
 */
@WikiCodeBlock(id = "one_2_many_example_parent_entity")
@TableInfo(tableName = "parent_entity")
public class One2ManyParentEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int id;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfoList(infoList = {
        @ForeignKeyInfo(name = "parent2childA",
                relatedObjectType = One2ManyChildEntityA.class,
                updateRule = ReferentialRuleType.RESTRICT,
                deleteRule = ReferentialRuleType.CASCADE,
                fieldMappings =  {@ForeignKeyFieldMapping(fromField = "id",toField = "parentId")})
        ,
        @ForeignKeyInfo(name = "parent2childB",
                relatedObjectType = One2ManyChildEntityB.class,
                updateRule = ReferentialRuleType.RESTRICT,
                deleteRule = ReferentialRuleType.CASCADE,
                fieldMappings =  {@ForeignKeyFieldMapping(fromField = "id",toField = "parentId")})
    })
    private Collection<One2ManyChildEntity> childEntities;

    public One2ManyParentEntity()
    {
        childEntities = new ArrayList<One2ManyChildEntity>();
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

    public Collection<One2ManyChildEntity> getChildEntities()
    {
        return childEntities;
    }

    public void setChildEntities(Collection<One2ManyChildEntity> childEntities)
    {
        this.childEntities = childEntities;
    }
}
