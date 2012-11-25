package dbgate.persist.support.columntest;

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
public class ColumnTestExtFactory
{
    public static Collection<IField> getFieldInfo(Class type)
    {
        ArrayList<IField> dbColumns = new ArrayList<IField>();

        if (type == ColumnTestEntityExts.class)
        {
            dbColumns.add(new DefaultColumn("idCol","id_col",true, ColumnType.INTEGER,true,new PrimaryKeyGenerator()));
            dbColumns.add(new DefaultColumn("longNotNull", ColumnType.LONG));
            dbColumns.add(new DefaultColumn("longNull", ColumnType.LONG,true));
            dbColumns.add(new DefaultColumn("booleanNotNull", ColumnType.BOOLEAN));
            dbColumns.add(new DefaultColumn("booleanNull", ColumnType.BOOLEAN,true));
            dbColumns.add(new DefaultColumn("charNotNull", ColumnType.CHAR));
            dbColumns.add(new DefaultColumn("charNull", ColumnType.CHAR,true));
            dbColumns.add(new DefaultColumn("intNotNull", ColumnType.INTEGER));
            dbColumns.add(new DefaultColumn("intNull", ColumnType.INTEGER,true));
            dbColumns.add(new DefaultColumn("dateNotNull", ColumnType.DATE));
            dbColumns.add(new DefaultColumn("dateNull", ColumnType.DATE,true));
            dbColumns.add(new DefaultColumn("doubleNotNull", ColumnType.DOUBLE));
            dbColumns.add(new DefaultColumn("doubleNull", ColumnType.DOUBLE,true));
            dbColumns.add(new DefaultColumn("floatNotNull", ColumnType.FLOAT));
            dbColumns.add(new DefaultColumn("floatNull", ColumnType.FLOAT,true));
            dbColumns.add(new DefaultColumn("timestampNotNull", ColumnType.TIMESTAMP));
            dbColumns.add(new DefaultColumn("timestampNull", ColumnType.TIMESTAMP,true));
            dbColumns.add(new DefaultColumn("varcharNotNull", ColumnType.VARCHAR));
            dbColumns.add(new DefaultColumn("varcharNull", ColumnType.VARCHAR,true));
        }

        return dbColumns;
    }

    public static ITable getTableInfo(Class type)
    {
        ITable tableInfo = null;
        if (type == ColumnTestEntityExts.class)
        {
            tableInfo = new DefaultTable("column_test_entity");
        }
        return tableInfo;
    }
}
