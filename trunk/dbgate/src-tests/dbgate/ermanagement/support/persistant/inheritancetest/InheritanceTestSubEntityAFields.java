package dbgate.ermanagement.support.persistant.inheritancetest;

import dbgate.DBColumnType;
import dbgate.ermanagement.DefaultDBColumn;
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

    public Map<Class,String> getTableNames()
    {
        Map<Class,String> map = super.getTableNames();
        map.put(InheritanceTestSubEntityAFields.class,"inheritance_test_suba");
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = super.getFieldInfo();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        dbColumns.add(new DefaultDBColumn("nameA",DBColumnType.VARCHAR));

        map.put(InheritanceTestSubEntityAFields.class,dbColumns);
        return map;
    }
}