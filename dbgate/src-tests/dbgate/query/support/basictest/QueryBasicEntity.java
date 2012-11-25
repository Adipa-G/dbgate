package dbgate.query.support.basictest;

import dbgate.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "query_basic")
public class QueryBasicEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfo(name = "fk_basic2join"
            ,relatedObjectType = QueryBasicJoinEntity.class
            , fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")
                               ,@ForeignKeyFieldMapping(fromField = "name", toField = "name")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE)
    private QueryBasicJoinEntity joinEntity;

    public QueryBasicEntity()
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

    public QueryBasicJoinEntity getJoinEntity()
    {
        return joinEntity;
    }

    public void setJoinEntity(QueryBasicJoinEntity joinEntity)
    {
        this.joinEntity = joinEntity;
    }
}