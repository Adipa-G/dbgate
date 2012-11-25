package dbgate.persist.support.treetest;

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
public class TreeTestExtFactory
{
    public static Collection<IField> getFieldInfo(Class type)
    {
        Collection<IField> fields = new ArrayList<IField>();

        if (type == TreeTestRootEntityExt.class)
        {
            fields.add(new DefaultColumn("idCol",true,false, ColumnType.INTEGER));
            fields.add(new DefaultColumn("name", ColumnType.VARCHAR));
            fields.add(new DefaultRelation("one2ManyEntities","fk_root2one2manyent"
                    , TreeTestOne2ManyEntityExt.class,new RelationColumnMapping[]{new RelationColumnMapping("idCol","idCol")}));
            fields.add(new DefaultRelation("one2OneEntity","fk_root2one2oneent"
                    , TreeTestOne2OneEntityExt.class,new RelationColumnMapping[]{new RelationColumnMapping("idCol","idCol")}));

        }
        else if (type == TreeTestOne2ManyEntityExt.class)
        {
            fields.add(new DefaultColumn("idCol",true,false, ColumnType.INTEGER));
            fields.add(new DefaultColumn("indexNo",true,false, ColumnType.INTEGER));
            fields.add(new DefaultColumn("name", ColumnType.VARCHAR));
        }
        else if (type == TreeTestOne2OneEntityExt.class)
        {
            fields.add(new DefaultColumn("idCol",true,false, ColumnType.INTEGER));
            fields.add(new DefaultColumn("name", ColumnType.VARCHAR));
        }
        return fields;
    }

    public static ITable getTableInfo(Class type)
    {
        ITable tableInfo = null;
        if (type == TreeTestRootEntityExt.class)
        {
            tableInfo =  new DefaultTable("tree_test_root");
        }
        else if (type == TreeTestOne2ManyEntityExt.class)
        {
            tableInfo =  new DefaultTable("tree_test_one2many");
        }
        else if (type == TreeTestOne2OneEntityExt.class)
        {
            tableInfo =  new DefaultTable("tree_test_one2one");
        }
        return tableInfo;
    }
}
