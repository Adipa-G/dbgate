package dbgate.ermanagement.dbabstractionlayer.metamanipulate;

import dbgate.ColumnType;
import dbgate.ITransaction;
import dbgate.ReferentialRuleType;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.*;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.MetaTable;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.mappings.ColumnTypeMapItem;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.mappings.ReferentialRuleTypeMapItem;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.support.MetaQueryHolder;
import dbgate.exceptions.migration.MetaDataException;
import dbgate.utility.DBMgtUtility;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 6:52:14 PM
 */
public abstract class AbstractMetaManipulate implements IMetaManipulate
{
    protected ArrayList<ColumnTypeMapItem> columnTypeMapItems;
    protected ArrayList<ReferentialRuleTypeMapItem> referentialRuleTypeMapItems;
    protected IDBLayer dbLayer;

    public AbstractMetaManipulate(IDBLayer dbLayer)
    {
        this.dbLayer = dbLayer;
        columnTypeMapItems = new ArrayList<>();
        referentialRuleTypeMapItems = new ArrayList<>();
    }

    @Override
    public void initialize(ITransaction tx) throws MetaDataException
    {
        fillDataMappings(tx);
        fillReferentialRuleMappings(tx);
    }

    protected void fillDataMappings(ITransaction tx) throws MetaDataException
    {
        DatabaseMetaData metaData;
        try
        {
            metaData = tx.getConnection().getMetaData();
            ResultSet resultSet = metaData.getTypeInfo();
            while (resultSet.next())
            {
                String typeName = resultSet.getString("TYPE_NAME");
                short sqlType = resultSet.getShort("DATA_TYPE");
                ColumnTypeMapItem mapItem = new ColumnTypeMapItem(typeName,ColumnType.getColumnType(sqlType));
                columnTypeMapItems.add(mapItem);
            }
            DBMgtUtility.close(resultSet);
        } catch (SQLException e)
        {
            e.printStackTrace();
            throw new MetaDataException(e.getMessage(), e);
        }
    }

    protected void fillReferentialRuleMappings(ITransaction tx)
    {
        referentialRuleTypeMapItems.add(new ReferentialRuleTypeMapItem(ReferentialRuleType.CASCADE, "0"));
        referentialRuleTypeMapItems.add(new ReferentialRuleTypeMapItem(ReferentialRuleType.RESTRICT, "1"));
    }

    protected abstract Collection<MetaTable> extractTableData(ITransaction tx) throws SQLException;

    protected abstract void extractColumnData(ITransaction tx, MetaTable table) throws SQLException;

    protected abstract void extractForeignKeyData(ITransaction tx, MetaTable table) throws SQLException;

    protected abstract void extractPrimaryKeyData(ITransaction tx, MetaTable table) throws SQLException;

    protected abstract String createCreateTableQuery(MetaComparisonTableGroup tableGroup);

    protected abstract String createDropTableQuery(MetaComparisonTableGroup tableGroup);

    protected abstract String createAlterTableQuery(MetaComparisonTableGroup tableGroup);

    protected abstract String createCreateColumnQuery(MetaComparisonTableGroup tableGroup,
                                                      MetaComparisonColumnGroup columnGroup);

    protected abstract String createDropColumnQuery(MetaComparisonTableGroup tableGroup,
                                                    MetaComparisonColumnGroup columnGroup);

    protected abstract String createAlterColumnQuery(MetaComparisonTableGroup tableGroup,
                                                     MetaComparisonColumnGroup columnGroup);

    protected abstract String createCreatePrimaryKeyQuery(MetaComparisonTableGroup tableGroup,
                                                          MetaComparisonPrimaryKeyGroup primaryKeyGroup);

    protected abstract String createDropPrimaryKeyQuery(MetaComparisonTableGroup tableGroup,
                                                        MetaComparisonPrimaryKeyGroup primaryKeyGroup);

    protected abstract String createCreateForeignKeyQuery(MetaComparisonTableGroup tableGroup,
                                                          MetaComparisonForeignKeyGroup foreignKeyGroup);

    protected abstract String createDropForeignKeyQuery(MetaComparisonTableGroup tableGroup,
                                                        MetaComparisonForeignKeyGroup foreignKeyGroup);

    @Override
    public ColumnType mapColumnTypeNameToType(String columnTypeName)
    {
        for (ColumnTypeMapItem typeMapItem : columnTypeMapItems)
        {
            if (typeMapItem.getName().equalsIgnoreCase(columnTypeName))
            {
                return typeMapItem.getColumnType();
            }
        }
        return null;
    }

    @Override
    public String mapColumnTypeToTypeName(ColumnType columnTypeId)
    {
        for (ColumnTypeMapItem typeMapItem : columnTypeMapItems)
        {
            if (typeMapItem.getColumnType() == columnTypeId)
            {
                return typeMapItem.getName();
            }
        }
        return null;
    }

    @Override
    public String getDefaultValueForType(ColumnType columnTypeId)
    {
        for (ColumnTypeMapItem typeMapItem : columnTypeMapItems)
        {
            if (typeMapItem.getColumnType() == columnTypeId)
            {
                return typeMapItem.getDefaultNonNullValue();
            }
        }
        return null;
    }

    @Override
    public ReferentialRuleType mapReferentialRuleNameToType(String ruleTypeName)
    {
        for (ReferentialRuleTypeMapItem ruleTypeMapItem : referentialRuleTypeMapItems)
        {
            if (ruleTypeMapItem.getRuleName().equals(ruleTypeName))
            {
                return ruleTypeMapItem.getRuleType();
            }
        }
        return null;
    }

    @Override
    public Collection<IMetaItem> getMetaData(ITransaction tx) throws MetaDataException
    {
        Collection<IMetaItem> items = new ArrayList<>();
        try
        {
            Collection<MetaTable> tables = extractTableData(tx);

            for (MetaTable table : tables)
            {
                items.add(table);

                extractColumnData(tx, table);

                extractPrimaryKeyData(tx, table);

                extractForeignKeyData(tx, table);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new MetaDataException(e.getMessage(), e);
        }
        return items;
    }

    @Override
    public Collection<MetaQueryHolder> createDbPathSQL(IMetaComparisonGroup metaComparisonGroup)
    {
        Collection<MetaQueryHolder> holders = new ArrayList<>();
        if (metaComparisonGroup instanceof MetaComparisonTableGroup)
        {
            MetaComparisonTableGroup tableGroup = (MetaComparisonTableGroup) metaComparisonGroup;
            if (metaComparisonGroup._shouldCreateInDB())
            {
                String query = createCreateTableQuery(tableGroup);
                holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_TABLE, MetaQueryHolder.OPERATION_TYPE_ADD,
                                                query));
            }
            if (metaComparisonGroup._shouldDeleteFromDB())
            {
                String query = createDropTableQuery(tableGroup);
                holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_TABLE,
                                                MetaQueryHolder.OPERATION_TYPE_DELETE, query));
            }
            if (metaComparisonGroup._shouldAlterInDB())
            {
                String query = createAlterTableQuery(tableGroup);
                holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_TABLE, MetaQueryHolder.OPERATION_TYPE_ALTER,
                                                query));
            }

            if (tableGroup.getPrimaryKey() != null)
            {
                MetaComparisonPrimaryKeyGroup primaryKeyGroup = tableGroup.getPrimaryKey();
                if (primaryKeyGroup._shouldCreateInDB()
                        || primaryKeyGroup._shouldAlterInDB())
                {
                    String query = createCreatePrimaryKeyQuery(tableGroup, primaryKeyGroup);
                    holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_PRIMARY_KEY,
                                                    MetaQueryHolder.OPERATION_TYPE_ADD, query));
                }
                if (tableGroup._shouldDeleteFromDB()
                        || primaryKeyGroup._shouldAlterInDB())
                {
                    String query = createDropPrimaryKeyQuery(tableGroup, primaryKeyGroup);
                    holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_PRIMARY_KEY,
                                                    MetaQueryHolder.OPERATION_TYPE_DELETE, query));
                }
            }

            for (MetaComparisonColumnGroup comparisonColumnGroup : tableGroup.getColumns())
            {
                if (comparisonColumnGroup._shouldCreateInDB())
                {
                    String query = createCreateColumnQuery(tableGroup, comparisonColumnGroup);
                    holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_COLUMN,
                                                    MetaQueryHolder.OPERATION_TYPE_ADD, query));
                }
                if (comparisonColumnGroup._shouldDeleteFromDB())
                {
                    String query = createDropColumnQuery(tableGroup, comparisonColumnGroup);
                    holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_COLUMN,
                                                    MetaQueryHolder.OPERATION_TYPE_DELETE, query));
                }
                if (comparisonColumnGroup._shouldAlterInDB())
                {
                    String query = createAlterColumnQuery(tableGroup, comparisonColumnGroup);
                    holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_COLUMN,
                                                    MetaQueryHolder.OPERATION_TYPE_ALTER, query));
                }
            }

            for (MetaComparisonForeignKeyGroup foreignKeyGroup : tableGroup.getForeignKeys())
            {
                if (foreignKeyGroup._shouldCreateInDB()
                        || foreignKeyGroup._shouldAlterInDB())
                {
                    String query = createCreateForeignKeyQuery(tableGroup, foreignKeyGroup);
                    holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_FOREIGN_KEY,
                                                    MetaQueryHolder.OPERATION_TYPE_ADD, query));
                }
                if (tableGroup._shouldDeleteFromDB()
                        || foreignKeyGroup._shouldAlterInDB())
                {
                    String query = createDropForeignKeyQuery(tableGroup, foreignKeyGroup);
                    holders.add(new MetaQueryHolder(MetaQueryHolder.OBJECT_TYPE_FOREIGN_KEY,
                                                    MetaQueryHolder.OPERATION_TYPE_DELETE, query));
                }
            }
        }

        return holders;
    }

    @Override
    public boolean Equals(IMetaItem iMetaItemA, IMetaItem iMetaItemB)
    {
        return (iMetaItemA.getItemType() == iMetaItemB.getItemType()
                && iMetaItemA.getName().equalsIgnoreCase(iMetaItemB.getName()));
    }
}
