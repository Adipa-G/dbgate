package dbgate.persist.support.version;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
@TableInfo(tableName = "version_test_root")
public class VersionColumnTestRootEntity extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;
    @ColumnInfo(columnType = ColumnType.VERSION)
    private int version;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;

    @ForeignKeyInfo(name = "fk_root2one2manyent",
            relatedObjectType = VersionColumnTestOne2ManyEntity.class,
            fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")},
            updateRule = ReferentialRuleType.RESTRICT,
            deleteRule = ReferentialRuleType.CASCADE)
    private Collection<VersionColumnTestOne2ManyEntity> one2ManyEntities;

    @ForeignKeyInfo(name = "fk_root2one2oneent",
            relatedObjectType = VersionColumnTestOne2OneEntity.class,
            fieldMappings = {@ForeignKeyFieldMapping(fromField = "idCol", toField = "idCol")},
            updateRule = ReferentialRuleType.RESTRICT,
            deleteRule = ReferentialRuleType.CASCADE)
    private VersionColumnTestOne2OneEntity one2OneEntity;

    public VersionColumnTestRootEntity()
    {
        one2ManyEntities = new ArrayList<VersionColumnTestOne2ManyEntity>();
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

    public Collection<VersionColumnTestOne2ManyEntity> getOne2ManyEntities()
    {
        return one2ManyEntities;
    }

    public void setOne2ManyEntities(Collection<VersionColumnTestOne2ManyEntity> one2ManyEntities)
    {
        this.one2ManyEntities = one2ManyEntities;
    }

    public VersionColumnTestOne2OneEntity getOne2OneEntity()
    {
        return one2OneEntity;
    }

    public void setOne2OneEntity(VersionColumnTestOne2OneEntity one2OneEntity)
    {
        this.one2OneEntity = one2OneEntity;
    }
}