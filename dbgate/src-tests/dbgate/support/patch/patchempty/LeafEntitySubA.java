package dbgate.support.patch.patchempty;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 11, 2010
 * Time: 9:51:32 PM
 * To change this template use File | Settings | File Templates.
 */

public class LeafEntitySubA extends LeafEntity
{
    private String someTextA;

    public String getSomeTextA()
    {
        return someTextA;
    }

    public void setSomeTextA(String someTextA)
    {
        this.someTextA = someTextA;
    }

    public Map<Class,ITable> getTableInfo()
    {
        Map<Class,ITable> map = super.getTableInfo();
        map.put(LeafEntitySubA.class,new DefaultTable("leaf_entity_a"));
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = super.getFieldInfo();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultColumn("someTextA", ColumnType.VARCHAR ));

        map.put(LeafEntitySubA.class,dbColumns);
        return map;
    }
}