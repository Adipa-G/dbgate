package dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.sqlservermm;

import dbgate.ColumnType;
import dbgate.ITransaction;
import dbgate.ReferentialRuleType;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonForeignKeyGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonPrimaryKeyGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonTableGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.MetaForeignKey;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.MetaForeignKeyColumnMapping;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.MetaPrimaryKey;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.MetaTable;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.defaultmm.DefaultMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.mappings.ColumnTypeMapItem;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.mappings.ReferentialRuleTypeMapItem;
import dbgate.exceptions.migration.MetaDataException;
import dbgate.utility.DBMgtUtility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 30, 2010
 * Time: 8:14:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlServerMetaManipulate extends DefaultMetaManipulate
{
    public SqlServerMetaManipulate(IDBLayer dbLayer)
    {
        super(dbLayer);
    }

    @Override
    protected void fillReferentialRuleMappings(ITransaction tx)
    {
        referentialRuleTypeMapItems.add(new ReferentialRuleTypeMapItem(ReferentialRuleType.CASCADE, "1"));
        referentialRuleTypeMapItems.add(new ReferentialRuleTypeMapItem(ReferentialRuleType.RESTRICT, "2"));
    }

    @Override
    protected  void fillDataMappings(ITransaction tx)
    {
        columnTypeMapItems.add(new ColumnTypeMapItem("INT", ColumnType.INTEGER, "0"));
        columnTypeMapItems.add(new ColumnTypeMapItem("BIT", ColumnType.BOOLEAN, "true"));
        columnTypeMapItems.add(new ColumnTypeMapItem("FLOAT", ColumnType.FLOAT, "0"));
        columnTypeMapItems.add(new ColumnTypeMapItem("CHAR", ColumnType.CHAR, "' '"));
        columnTypeMapItems.add(new ColumnTypeMapItem("DATE", ColumnType.DATE, "1981/10/12"));
        columnTypeMapItems.add(new ColumnTypeMapItem("DECIMAL", ColumnType.DOUBLE, "0"));
        columnTypeMapItems.add(new ColumnTypeMapItem("BIGINT", ColumnType.LONG, "0"));
        columnTypeMapItems.add(new ColumnTypeMapItem("TIMESTAMP", ColumnType.TIMESTAMP, "1961/11/17"));
        columnTypeMapItems.add(new ColumnTypeMapItem("VARCHAR", ColumnType.VARCHAR, "''"));
        columnTypeMapItems.add(new ColumnTypeMapItem("NVARCHAR", ColumnType.VARCHAR, "''"));
    }

    @Override
    protected Collection<MetaTable> extractTableData(ITransaction tx) throws SQLException
    {
        List<MetaTable> tables = new ArrayList<>();

        PreparedStatement ps = tx.getConnection().prepareStatement("SELECT name FROM sys.Tables;");
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            MetaTable metaTable = new MetaTable();
            metaTable.setName(rs.getString("name"));
            tables.add(metaTable);
        }

        DBMgtUtility.close(rs);
        DBMgtUtility.close(ps);

        return tables;
    }

    @Override
    protected String createCreatePrimaryKeyQuery(MetaComparisonTableGroup tableGroup,
                                                 MetaComparisonPrimaryKeyGroup primaryKeyGroup)
    {
        MetaTable requiredTable = (MetaTable)tableGroup.getRequiredItem();
        MetaPrimaryKey primaryKey = primaryKeyGroup.getRequiredItem();
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE [");
        sb.append(requiredTable.getName());
        sb.append("] ADD CONSTRAINT ");
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
    protected String createDropPrimaryKeyQuery(MetaComparisonTableGroup tableGroup,
                                               MetaComparisonPrimaryKeyGroup primaryKeyGroup)
    {
        MetaTable requiredTable = tableGroup.getExistingItem();
        MetaPrimaryKey primaryKey = primaryKeyGroup.getExistingItem();
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE [");
        sb.append(requiredTable.getName());
        sb.append("] DROP CONSTRAINT ");
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
        sb.append("ALTER TABLE [");
        sb.append(requiredTable.getName());
        sb.append("] ADD CONSTRAINT ");
        sb.append(metaForeignKey.getName());
        sb.append(" FOREIGN KEY ");
        sb.append(" ( ");

        Iterator<MetaForeignKeyColumnMapping> enumerator = metaForeignKey.getColumnMappings().iterator();
        int i = 0;
        while (enumerator.hasNext())
        {
            MetaForeignKeyColumnMapping mapping = enumerator.next();
            if (i > 0)
            {
                sb.append(" , ");
            }
            sb.append(mapping.getFromColumn());
            i++;
        }
        sb.append(" ) ");
        sb.append(" REFERENCES ");
        sb.append(metaForeignKey.getToTable());
        sb.append(" ( ");

        enumerator = metaForeignKey.getColumnMappings().iterator();
        i = 0;
        while (enumerator.hasNext())
        {
            MetaForeignKeyColumnMapping mapping = enumerator.next();
            if (i > 0)
            {
                sb.append(" , ");
            }
            sb.append(mapping.getToColumn());
            i++;
        }
        sb.append(" ) ");
        sb.append(" ON DELETE ").append(metaForeignKey.getDeleteRule());
        if (metaForeignKey.getUpdateRule() != ReferentialRuleType.RESTRICT)
        {
            sb.append(" ON UPDATE ").append(metaForeignKey.getUpdateRule());
        }
        return sb.toString();
    }

    @Override
    protected String createDropForeignKeyQuery(MetaComparisonTableGroup tableGroup,
                                               MetaComparisonForeignKeyGroup foreignKeyGroup)
    {
        MetaTable requiredTable = (MetaTable)tableGroup.getRequiredItem();
        MetaForeignKey metaForeignKey = (MetaForeignKey)foreignKeyGroup.getRequiredItem();
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE [");
        sb.append(requiredTable.getName());
        sb.append("] DROP CONSTRAINT ");
        sb.append(metaForeignKey.getToTable());
        return sb.toString();
    }
}
