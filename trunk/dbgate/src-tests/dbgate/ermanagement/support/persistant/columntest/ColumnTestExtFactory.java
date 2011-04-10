package dbgate.ermanagement.support.persistant.columntest;

import dbgate.DBColumnType;
import dbgate.ermanagement.DefaultDBColumn;
import dbgate.ermanagement.IField;

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
            dbColumns.add(new DefaultDBColumn("idCol","id_col",true, DBColumnType.INTEGER,true,new PrimaryKeyGenerator()));
            dbColumns.add(new DefaultDBColumn("longNotNull",DBColumnType.LONG));
            dbColumns.add(new DefaultDBColumn("longNull",DBColumnType.LONG,true));
            dbColumns.add(new DefaultDBColumn("booleanNotNull",DBColumnType.BOOLEAN));
            dbColumns.add(new DefaultDBColumn("booleanNull",DBColumnType.BOOLEAN,true));
            dbColumns.add(new DefaultDBColumn("charNotNull",DBColumnType.CHAR));
            dbColumns.add(new DefaultDBColumn("charNull",DBColumnType.CHAR,true));
            dbColumns.add(new DefaultDBColumn("intNotNull",DBColumnType.INTEGER));
            dbColumns.add(new DefaultDBColumn("intNull",DBColumnType.INTEGER,true));
            dbColumns.add(new DefaultDBColumn("dateNotNull",DBColumnType.DATE));
            dbColumns.add(new DefaultDBColumn("dateNull",DBColumnType.DATE,true));
            dbColumns.add(new DefaultDBColumn("doubleNotNull",DBColumnType.DOUBLE));
            dbColumns.add(new DefaultDBColumn("doubleNull",DBColumnType.DOUBLE,true));
            dbColumns.add(new DefaultDBColumn("floatNotNull",DBColumnType.FLOAT));
            dbColumns.add(new DefaultDBColumn("floatNull",DBColumnType.FLOAT,true));
            dbColumns.add(new DefaultDBColumn("timestampNotNull",DBColumnType.TIMESTAMP));
            dbColumns.add(new DefaultDBColumn("timestampNull",DBColumnType.TIMESTAMP,true));
            dbColumns.add(new DefaultDBColumn("varcharNotNull",DBColumnType.VARCHAR));
            dbColumns.add(new DefaultDBColumn("varcharNull",DBColumnType.VARCHAR,true));
        }

        return dbColumns;
    }

    public static String getTableNames(Class type)
    {
        String tableName = null;
        if (type == ColumnTestEntityExts.class)
        {
            tableName = "column_test_entity";
        }
        return tableName;
    }
}
