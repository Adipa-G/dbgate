package dbgate.patch.support.patchempty;

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

public class LeafEntitySubB extends LeafEntity
{
    private String someTextB;

    public String getSomeTextB()
    {
        return someTextB;
    }

    public void setSomeTextB(String someTextB)
    {
        this.someTextB = someTextB;
    }

    public Map<Class,ITable> getTableInfo()
    {
        Map<Class,ITable> map = super.getTableInfo();
        map.put(LeafEntitySubB.class,new DefaultTable("leaf_entity_b"));
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = super.getFieldInfo();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultColumn("someTextB", ColumnType.VARCHAR ));

        map.put(LeafEntitySubB.class,dbColumns);
        return map;
    }
}