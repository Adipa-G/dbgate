package dbgate.ermanagement.support.persistant.version;

import dbgate.DBColumnType;
import dbgate.ermanagement.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@DBTableInfo(tableName = "version_test_root")
public class VersionGeneralTestRootEntity extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int idCol;
    @DBColumnInfo(columnType = DBColumnType.INTEGER)
    private int version;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;

    @ForeignKeyInfo(name = "fk_root2one2manyent"
            ,relatedObjectType = VersionGeneralTestOne2ManyEntity.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE)
    private Collection<VersionGeneralTestOne2ManyEntity> one2ManyEntities;

    @ForeignKeyInfo(name = "fk_root2one2oneent"
            ,relatedObjectType = VersionGeneralTestOne2OneEntity.class
            ,columnMappings = {@ForeignKeyColumnMapping(fromField = "idCol", toField = "idCol")}
            ,updateRule = ReferentialRuleType.RESTRICT
            ,deleteRule = ReferentialRuleType.CASCADE)
    private VersionGeneralTestOne2OneEntity one2OneEntity;

    public VersionGeneralTestRootEntity()
    {
        one2ManyEntities = new ArrayList<VersionGeneralTestOne2ManyEntity>();
    }

    public int getIdCol()
    {
        return idCol;
    }

    public void setIdCol(int idCol)
    {
        this.idCol = idCol;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Collection<VersionGeneralTestOne2ManyEntity> getOne2ManyEntities()
    {
        return one2ManyEntities;
    }

    public void setOne2ManyEntities(Collection<VersionGeneralTestOne2ManyEntity> one2ManyEntities)
    {
        this.one2ManyEntities = one2ManyEntities;
    }

    public VersionGeneralTestOne2OneEntity getOne2OneEntity()
    {
        return one2OneEntity;
    }

    public void setOne2OneEntity(VersionGeneralTestOne2OneEntity one2OneEntity)
    {
        this.one2OneEntity = one2OneEntity;
    }
}