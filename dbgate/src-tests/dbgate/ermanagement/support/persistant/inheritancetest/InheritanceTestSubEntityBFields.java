package dbgate.ermanagement.support.persistant.inheritancetest;

import dbgate.ColumnType;
import dbgate.ermanagement.DefaultColumn;
import dbgate.ermanagement.IField;

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

    public Map<Class,String> getTableNames()
    {
        Map<Class,String> map = super.getTableNames();
        map.put(InheritanceTestSubEntityBFields.class,"inheritance_test_subb");
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