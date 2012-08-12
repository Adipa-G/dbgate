package dbgate.utility.support.dbutility;

import dbgate.*;
import dbgate.AbstractManagedEntity;
import dbgate.DefaultColumn;
import dbgate.IField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 11, 2010
 * Time: 12:01:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class RootEntity extends AbstractManagedEntity
{
    private int id;
    private String name;

    public RootEntity()
    {
    }

    public RootEntity(int id, String name)
    {
        this.id = id;
        this.name = name;
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

    public Map<Class,String> getTableNames()
    {
        Map<Class,String> map = new HashMap<Class, String>();
        map.put(this.getClass(),"root_entity");
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = new HashMap<Class, Collection<IField>>();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultColumn("id",true, ColumnType.INTEGER));
        dbColumns.add(new DefaultColumn("name", ColumnType.VARCHAR ));

        map.put(this.getClass(),dbColumns);
        return map;
    }
}
