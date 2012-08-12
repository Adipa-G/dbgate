package dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.defaultmm;

import dbgate.ColumnType;
import dbgate.utility.DBMgtUtility;
import dbgate.exceptions.migration.MetaDataException;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.AbstractMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonColumnGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonForeignKeyGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonPrimaryKeyGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonTableGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.*;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.mappings.ColumnTypeMapItem;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 7:00:37 PM
 */
public class DefaultMetaManipulate extends AbstractMetaManipulate
{
    public DefaultMetaManipulate(IDBLayer dbLayer)
    {
        super(dbLayer);
    }

    @Override
    protected void fillDataMappings(Connection con) throws MetaDataException
    {
        columnTypeMapItems.add(new ColumnTypeMapItem("INTEGER", ColumnType.INTEGER,"0"));
        columnTypeMapItems.add(new ColumnTypeMapItem("CHAR", ColumnType.BOOLEAN,"true"));
        columnTypeMapItems.add(new ColumnTypeMapItem("FLOAT", ColumnType.FLOAT,"0"));
        columnTypeMapItems.add(new ColumnTypeMapItem("CHAR", ColumnType.CHAR,"' '"));
        columnTypeMapItems.add(new ColumnTypeMapItem("DATE", ColumnType.DATE,"1981/10/12"));
        columnTypeMapItems.add(new ColumnTypeMapItem("DOUBLE", ColumnType.DOUBLE,"0"));
        columnTypeMapItems.add(new ColumnTypeMapItem("BIGINT", ColumnType.LONG,"0"));
        columnTypeMapItems.add(new ColumnTypeMapItem("TIMESTAMP", ColumnType.TIMESTAMP,"1981/10/12"));
        columnTypeMapItems.add(new ColumnTypeMapItem("VARCHAR", ColumnType.VARCHAR,"''"));
    }

    @Override
    protected void extractColumnData(DatabaseMetaData metaData, MetaTable table) throws SQLException
    {
        ResultSet columnResultSet = metaData.getColumns(null,null,table.getName(),null);
        while (columnResultSet.next())
        {
            MetaColumn column = new MetaColumn();
            table.getColumns().add(column);
            column.setName(columnResultSet.getString("COLUMN_NAME"));
            column.setSize(columnResultSet.getInt("COLUMN_SIZE"));
            column.setColumnType(mapColumnTypeNameToType(columnResultSet.getString("TYPE_NAME")));
            column.setNull(columnResultSet.getBoolean("NULLABLE"));
        }
        DBMgtUtility.close(columnResultSet);
    }

    @Override
    protected void extractPrimaryKeyData(DatabaseMetaData metaData, MetaTable table) throws SQLException
    {
        ResultSet primaryKeyResultSet = metaData.getPrimaryKeys(null,null,table.getName());
        HashMap<Integer,String> keyColMap = new HashMap<Integer, String>();
        while (primaryKeyResultSet.next())
        {
            if (table.getPrimaryKey() == null)
            {
                MetaPrimaryKey primaryKey = new MetaPrimaryKey();
                table.setPrimaryKey(primaryKey);
                primaryKey.setName(primaryKeyResultSet.getString("PK_NAME"));
            }
            keyColMap.put(primaryKeyResultSet.getInt("KEY_SEQ"),primaryKeyResultSet.getString("COLUMN_NAME"));
        }
        if (table.getPrimaryKey() != null)
        {
            ArrayList<Comparable> list = new ArrayList<Comparable>(keyColMap.keySet());
            Collections.sort(list);
            for (Comparable comparable : list)
            {
                table.getPrimaryKey().getColumnNames().add(keyColMap.get(comparable));
            }
        }
        DBMgtUtility.close(primaryKeyResultSet);
    }

    @Override
    protected void extractForeignKeyData(DatabaseMetaData metaData, MetaTable table) throws SQLException
    {
        ResultSet foreignKeyResultSet = metaData.getExportedKeys(null,null,table.getName());
        HashMap<String, MetaForeignKey> foreignKeyMap = new HashMap<String, MetaForeignKey>();

        HashMap<String,HashMap<Integer,String>> fromTableColMap = new HashMap<String, HashMap<Integer, String>>();
        HashMap<String,HashMap<Integer,String>> toTableColMap = new HashMap<String, HashMap<Integer, String>>();

        while (foreignKeyResultSet.next())
        {
            String fkName = foreignKeyResultSet.getString("FK_NAME");
            if (!foreignKeyMap.containsKey(fkName))
            {
                MetaForeignKey foreignKey = new MetaForeignKey();
                table.getForeignKeys().add(foreignKey);
                foreignKeyMap.put(fkName,foreignKey);
                foreignKey.setName(fkName);
                foreignKey.setToTable(foreignKeyResultSet.getString("FKTABLE_NAME"));
                foreignKey.setUpdateRule(mapReferentialRuleNameToType(foreignKeyResultSet.getString("UPDATE_RULE")));
                foreignKey.setDeleteRule(mapReferentialRuleNameToType(foreignKeyResultSet.getString("DELETE_RULE")));

                fromTableColMap.put(fkName,new HashMap<Integer, String>());
                toTableColMap.put(fkName,new HashMap<Integer, String>());
            }

            fromTableColMap.get(fkName).put(foreignKeyResultSet.getInt("KEY_SEQ"),foreignKeyResultSet.getString("PKCOLUMN_NAME"));
            toTableColMap.get(fkName).put(foreignKeyResultSet.getInt("KEY_SEQ"),foreignKeyResultSet.getString("FKCOLUMN_NAME"));
        }

        for (String key : foreignKeyMap.keySet())
        {
            MetaForeignKey foreignKey = foreignKeyMap.get(key);

            ArrayList<Comparable> fromList = new ArrayList<Comparable>( fromTableColMap.get(key).keySet());
            Collections.sort(fromList);

            ArrayList<Comparable> toList = new ArrayList<Comparable>(toTableColMap.get(key).keySet());
            Collections.sort(toList);

            Iterator<Comparable> toListIterator = toList.iterator();
            for (Comparable comparable : fromList)
            {
                String fromCol = fromTableColMap.get(key).get(comparable);
                String toCol = null;
                if (toListIterator.hasNext())
                {
                    toCol = toTableColMap.get(key).get(toListIterator.next());
                }
                foreignKey.getColumnMappings().add(new MetaForeignKeyColumnMapping(fromCol,toCol));
            }
        }
        DBMgtUtility.close(foreignKeyResultSet);
    }
    
    @Override
    protected String createCreateTableQuery(MetaComparisonTableGroup tableGroup)
    {
        MetaTable metaTable = tableGroup.getRequiredItem();

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(metaTable.getName());
        sb.append(" ( ");
        boolean first = true;

        for (MetaColumn metaColumn : metaTable.getColumns())
        {
            if (!first)
            {
                sb.append(" ,\n ");
            }
            else
            {
                first = false;
            }
            sb.append(metaColumn.getName());
            sb.append(" ");
            if (metaColumn.getColumnType() == ColumnType.CHAR
                    || metaColumn.getColumnType() == ColumnType.VARCHAR)
            {
                sb.append(mapColumnTypeToTypeName(metaColumn.getColumnType()));
                sb.append("(");
                sb.append(metaColumn.getSize());
                sb.append(")");
            }
            else
            {
                sb.append(mapColumnTypeToTypeName(metaColumn.getColumnType()));
            }
            sb.append(" ");
            sb.append(metaColumn.isNull() ? "" : "NOT NULL");
            sb.append(" ");
        }

        sb.append(")");
        return sb.toString();
    }

    @Override
    protected String createDropTableQuery(MetaComparisonTableGroup tableGroup)
    {
        MetaTable metaTable = tableGroup.getExistingItem();

        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ");
        sb.append(metaTable.getName());
        return sb.toString();
    }

    @Override
    protected String createAlterTableQuery(MetaComparisonTableGroup tableGroup)
    {
        return null;
    }

    @Override
    protected String createCreateColumnQuery(MetaComparisonTableGroup tableGroup, MetaComparisonColumnGroup columnGroup)
    {
        MetaTable metaTable = tableGroup.getRequiredItem();
        MetaColumn metaColumn = columnGroup.getRequiredItem();

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(metaTable.getName());
        sb.append(" ADD ");
        sb.append(metaColumn.getName());
        sb.append(" ");
        if (metaColumn.getColumnType() == ColumnType.CHAR
                || metaColumn.getColumnType() == ColumnType.VARCHAR)
        {
            sb.append(mapColumnTypeToTypeName(metaColumn.getColumnType()));
            sb.append("(");
            sb.append(metaColumn.getSize());
            sb.append(")");
        }
        else
        {
            sb.append(mapColumnTypeToTypeName(metaColumn.getColumnType()));
        }
        sb.append(" DEFAULT ");
        if (!metaColumn.isNull())
        {
            String defaultValue = getDefaultValueForType(metaColumn.getColumnType());
            if (defaultValue != null)
            {
                sb.append(defaultValue);
            }
        }
        else
        {
            sb.append("NULL");
        }

        sb.append(" ");
        sb.append(metaColumn.isNull() ? "" : "NOT NULL");
        sb.append(" ");

        return sb.toString();   
    }

    @Override
    protected String createDropColumnQuery(MetaComparisonTableGroup tableGroup, MetaComparisonColumnGroup columnGroup)
    {
        MetaTable metaTable = tableGroup.getRequiredItem();
        MetaColumn metaColumn = columnGroup.getExistingItem();

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(metaTable.getName());
        sb.append(" DROP COLUMN ");
        sb.append(metaColumn.getName());
        sb.append(" ");

        return sb.toString();
    }

    @Override
    protected String createAlterColumnQuery(MetaComparisonTableGroup tableGroup, MetaComparisonColumnGroup columnGroup)
    {
        MetaTable metaTable = tableGroup.getRequiredItem();
        MetaColumn metaColumn = columnGroup.getRequiredItem();

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(metaTable.getName());
        sb.append(" ALTER ");
        sb.append(metaColumn.getName());
        sb.append(" SET DATA TYPE ");
        if (metaColumn.getColumnType() == ColumnType.CHAR
                || metaColumn.getColumnType() == ColumnType.VARCHAR)
        {
            sb.append(mapColumnTypeToTypeName(metaColumn.getColumnType()));
            sb.append("(");
            sb.append(metaColumn.getSize());
            sb.append(")");
        }
        else
        {
            sb.append(mapColumnTypeToTypeName(metaColumn.getColumnType()));
        }

        return sb.toString();
    }

    @Override
    protected String createCreatePrimaryKeyQuery(MetaComparisonTableGroup tableGroup, MetaComparisonPrimaryKeyGroup primaryKeyGroup)
    {
        MetaTable requiredTable = tableGroup.getRequiredItem();
        MetaPrimaryKey primaryKey = primaryKeyGroup.getRequiredItem();

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(requiredTable.getName());
        sb.append(" ADD CONSTRAINT ");
        sb.append(primaryKey.getName());
        sb.append(" PRIMARY KEY ( ");

        boolean first = true;
        for (String columnName : primaryKey.getColumnNames())
        {
            if (!first)
            {
                sb.append(" ,\n ");
            }
            else
            {
                first = false;
            }
            sb.append(columnName);
            sb.append(" ");
        }
        
        sb.append(")");
        return sb.toString();
    }

    @Override
    protected String createDropPrimaryKeyQuery(MetaComparisonTableGroup tableGroup, MetaComparisonPrimaryKeyGroup primaryKeyGroup)
    {
        MetaTable requiredTable = tableGroup.getExistingItem();
        MetaPrimaryKey primaryKey = primaryKeyGroup.getExistingItem();

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(requiredTable.getName());
        sb.append(" DROP CONSTRAINT ");
        sb.append(primaryKey.getName());
        return sb.toString();
    }

    @Override
    protected String createCreateForeignKeyQuery(MetaComparisonTableGroup tableGroup,
                                                 MetaComparisonForeignKeyGroup foreignKeyGroup)
    {
        MetaTable requiredTable = tableGroup.getRequiredItem();
        MetaForeignKey metaForeignKey = foreignKeyGroup.getRequiredItem();

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(metaForeignKey.getToTable());
        sb.append(" ADD CONSTRAINT ");
        sb.append(metaForeignKey.getName());
        sb.append(" FOREIGN KEY ");

        sb.append(" ( ");
        Iterator<MetaForeignKeyColumnMapping> iterator = metaForeignKey.getColumnMappings().iterator();
        int i = 0;
        while (iterator.hasNext())
        {
            MetaForeignKeyColumnMapping mapping = iterator.next();
            if (i > 0)
            {
                sb.append(" , ");
            }
            sb.append(mapping.getToColumn());
            i++;
        }
        sb.append(" ) ");

        sb.append(" REFERENCES ");
        sb.append(requiredTable.getName());

        sb.append(" ( ");
        iterator = metaForeignKey.getColumnMappings().iterator();
        i = 0;
        while (iterator.hasNext())
        {
            MetaForeignKeyColumnMapping mapping = iterator.next();
            if (i > 0)
            {
                sb.append(" , ");
            }
            sb.append(mapping.getFromColumn());
            i++;
        }
        sb.append(" ) ");
        sb.append(" ON DELETE ").append(metaForeignKey.getDeleteRule());
        sb.append(" ON UPDATE ").append(metaForeignKey.getUpdateRule());

        return sb.toString();
    }

    @Override
    protected String createDropForeignKeyQuery(MetaComparisonTableGroup tableGroup,
                                               MetaComparisonForeignKeyGroup foreignKeyGroup)
    {
        MetaTable requiredTable = tableGroup.getExistingItem();
        MetaForeignKey metaForeignKey = foreignKeyGroup.getExistingItem();

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(requiredTable.getName());
        sb.append(" DROP CONSTRAINT ");
        sb.append(metaForeignKey.getName());
        return sb.toString();
    }


}
