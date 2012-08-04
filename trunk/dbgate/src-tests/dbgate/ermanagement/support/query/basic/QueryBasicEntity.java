package dbgate.ermanagement.support.query.basic;

import dbgate.DBColumnType;
import dbgate.ermanagement.*;
import dbgate.ermanagement.support.persistant.changetracker.ChangeTrackerTestOne2OneEntity;
import dbgate.ermanagement.support.persistant.lazy.LazyOne2ManyEntity;
import dbgate.ermanagement.support.persistant.lazy.LazyOne2OneEntity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@DBTableInfo(tableName = "query_basic")
public class QueryBasicEntity extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int idCol;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfo(name = "fk_basic2join"
            ,relatedObjectType = QueryBasicJoinEntity.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")
                               ,@ForeignKeyColumnMapping(fromField = "name", toField = "name")}
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