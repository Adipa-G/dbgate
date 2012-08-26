package dbgate.support.persistant.treetest;

import dbgate.AbstractManagedEntity;
import dbgate.ColumnType;
import dbgate.DefaultColumn;
import dbgate.IField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
public class TreeTestOne2ManyEntityFields extends AbstractManagedEntity implements ITreeTestOne2ManyEntity
{
    private int idCol;
    private int indexNo;
    private String name;

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

    public Map<Class,String> getTableNames()
    {
        Map<Class,String> map = new HashMap<Class, String>();
        map.put(this.getClass(),"tree_test_one2many");
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = new HashMap<Class, Collection<IField>>();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultColumn("idCol",true,false, ColumnType.INTEGER));
        dbColumns.add(new DefaultColumn("indexNo",true,false, ColumnType.INTEGER));
        dbColumns.add(new DefaultColumn("name", ColumnType.VARCHAR));

        map.put(this.getClass(),dbColumns);
        return map;
    }
}