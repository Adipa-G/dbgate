package dbgate.ermanagement.support.patch.patchempty;

import dbgate.ColumnType;
import dbgate.ermanagement.AbstractManagedEntity;
import dbgate.ermanagement.DefaultColumn;
import dbgate.ermanagement.IColumn;
import dbgate.ermanagement.IField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 11, 2010
 * Time: 9:51:32 PM
 * To change this template use File | Settings | File Templates.
 */

public class LeafEntity  extends AbstractManagedEntity
{
    private int idCol;
    private int indexNo;
    private String someText;

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

    public String getSomeText()
    {
        return someText;
    }

    public void setSomeText(String someText)
    {
        this.someText = someText;
    }

    public Map<Class,String> getTableNames()
    {
        Map<Class,String> map = new HashMap<Class, String>();
        map.put(LeafEntity.class,"leaf_entity");
        return map;
    }

    public Map<Class,Collection<IField>> getFieldInfo()
    {
        Map<Class,Collection<IField>> map = new HashMap<Class, Collection<IField>>();
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        IColumn idCol = new DefaultColumn("idCol",true, ColumnType.INTEGER);
        idCol.setSubClassCommonColumn(true);
        dbColumns.add(idCol);
        IColumn indexCol = new DefaultColumn("indexNo",true, ColumnType.INTEGER);
        indexCol.setSubClassCommonColumn(true);
        dbColumns.add(indexCol);
        dbColumns.add(new DefaultColumn("someText", ColumnType.VARCHAR ));

        map.put(LeafEntity.class,dbColumns);
        return map;
    }
}