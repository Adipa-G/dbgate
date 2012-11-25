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
public class InheritanceTestSubEntityBFields extends InheritanceTestSuperEntityFields implements IInheritanceTestSubEntityB
{
    private String nameB;

    public String getNameB()
    {
        return nameB;
    }

    public void setNameB(String nameB)
    {
        this.nameB = nameB;
    }

    public Map<Class,ITable> getTableInfo()
    {
        Map<Class,ITable> map = super.getTableInfo();
        map.put(InheritanceTestSubEntityBFields.class,new DefaultTable("inheritance_test_subb"));
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = super.getFieldInfo();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultColumn("nameB", ColumnType.VARCHAR));

        map.put(InheritanceTestSubEntityBFields.class,dbColumns);
        return map;
    }
}