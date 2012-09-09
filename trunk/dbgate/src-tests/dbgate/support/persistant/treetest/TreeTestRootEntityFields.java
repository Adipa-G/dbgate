package dbgate.support.persistant.treetest;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:59 AM
 */
public class TreeTestRootEntityFields extends AbstractManagedEntity implements ITreeTestRootEntity
{
    private int idCol;
    private String name;
    private Collection<ITreeTestOne2ManyEntity> one2ManyEntities;
    private ITreeTestOne2OneEntity one2OneEntity;

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

    public Map<Class,ITable> getTableInfo()
    {
        Map<Class,ITable> map = new HashMap<>();
        map.put(this.getClass(),new DefaultTable("tree_test_root"));
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = new HashMap<Class, Collection<IField>>();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultColumn("idCol",true,false, ColumnType.INTEGER));
        dbColumns.add(new DefaultColumn("name", ColumnType.VARCHAR));
        dbColumns.add(new DefaultRelation("one2ManyEntities","fk_root2one2manyent"
                , TreeTestOne2ManyEntityFields.class,new RelationColumnMapping[]{new RelationColumnMapping("idCol","idCol")}));
        dbColumns.add(new DefaultRelation("one2OneEntity","fk_root2one2oneent"
                , TreeTestOne2OneEntityFields.class,new RelationColumnMapping[]{new RelationColumnMapping("idCol","idCol")}));

        map.put(this.getClass(),dbColumns);
        return map;
    }
}