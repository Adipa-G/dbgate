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
 * Time: 12:23:11 PM
 */
public class TreeTestOne2OneEntityFields extends AbstractManagedEntity implements ITreeTestOne2OneEntity
{
    private int idCol;
    private String name;

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

    public Map<Class,ITable> getTableInfo()
    {
        Map<Class,ITable> map = new HashMap<>();
        map.put(this.getClass(),new DefaultTable("tree_test_one2one"));
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = new HashMap<Class, Collection<IField>>();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultColumn("idCol",true,false, ColumnType.INTEGER));
        dbColumns.add(new DefaultColumn("name", ColumnType.VARCHAR));

        map.put(this.getClass(),dbColumns);
        return map;
    }
}