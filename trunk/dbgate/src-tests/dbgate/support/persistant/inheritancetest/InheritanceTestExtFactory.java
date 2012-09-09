package dbgate.support.persistant.inheritancetest;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Nov 26, 2010
 * Time: 6:32:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class InheritanceTestExtFactory
{
    public static Collection<IField> getFieldInfo(Class type)
    {
        Collection<IField> fields = new ArrayList<IField>();

        if (type == InheritanceTestSuperEntityExt.class)
        {
            DefaultColumn idCol = new DefaultColumn("idCol",true,false, ColumnType.INTEGER);
            idCol.setSubClassCommonColumn(true);
            fields.add(idCol);
            fields.add(new DefaultColumn("name", ColumnType.VARCHAR));
        }
        else if (type == InheritanceTestSubEntityAExt.class)
        {
            DefaultColumn idCol = new DefaultColumn("idCol",true,false, ColumnType.INTEGER);
            idCol.setSubClassCommonColumn(true);
            fields.add(idCol);
            fields.add(new DefaultColumn("nameA", ColumnType.VARCHAR));
        }
        else if (type == InheritanceTestSubEntityBExt.class)
        {
            DefaultColumn idCol = new DefaultColumn("idCol",true,false, ColumnType.INTEGER);
            idCol.setSubClassCommonColumn(true);
            fields.add(idCol);
            fields.add(new DefaultColumn("nameB", ColumnType.VARCHAR));
        }
        return fields;
    }

    public static ITable getTableInfo(Class type)
    {
        ITable tableInfo = null;
        if (type == InheritanceTestSuperEntityExt.class)
        {
            tableInfo =  new DefaultTable("inheritance_test_super");
        }
        else if (type == InheritanceTestSubEntityAExt.class)
        {
            tableInfo =  new DefaultTable("inheritance_test_suba");
        }
        else if (type == InheritanceTestSubEntityBExt.class)
        {
            tableInfo =  new DefaultTable("inheritance_test_subb");
        }
        return tableInfo;
    }
}
