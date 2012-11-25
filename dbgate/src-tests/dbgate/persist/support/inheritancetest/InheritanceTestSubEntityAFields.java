package dbgate.persist.support.inheritancetest;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
public class InheritanceTestSubEntityAFields extends InheritanceTestSuperEntityFields implements IInheritanceTestSubEntityA
{
    private String nameA;

    public String getNameA()
    {
        return nameA;
    }

    public void setNameA(String nameA)
    {
        this.nameA = nameA;
    }

    public Map<Class,ITable> getTableInfo()
    {
        Map<Class,ITable> map = super.getTableInfo();
        map.put(InheritanceTestSubEntityAFields.class,new DefaultTable("inheritance_test_suba"));
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = super.getFieldInfo();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultColumn("nameA", ColumnType.VARCHAR));

        map.put(InheritanceTestSubEntityAFields.class,dbColumns);
        return map;
    }
}