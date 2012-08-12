package dbgate.support.persistant.inheritancetest;

import dbgate.*;
import dbgate.AbstractManagedEntity;
import dbgate.DefaultColumn;
import dbgate.IField;
import dbgate.support.persistant.treetest.ITreeTestOne2OneEntity;

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
public class InheritanceTestSuperEntityFields extends AbstractManagedEntity implements ITreeTestOne2OneEntity
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

    public Map<Class,String> getTableNames()
    {
        Map<Class,String> map = new HashMap<Class, String>();
        map.put(InheritanceTestSuperEntityFields.class,"inheritance_test_super");
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = new HashMap<Class, Collection<IField>>();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        DefaultColumn idCol = new DefaultColumn("idCol",true,false, ColumnType.INTEGER);
        idCol.setSubClassCommonColumn(true);
        dbColumns.add(idCol);
        dbColumns.add(new DefaultColumn("name", ColumnType.VARCHAR));

        map.put(InheritanceTestSuperEntityFields.class,dbColumns);
        return map;
    }
}