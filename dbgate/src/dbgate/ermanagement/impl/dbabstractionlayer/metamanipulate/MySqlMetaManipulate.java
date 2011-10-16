package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate;

import dbgate.DBColumnType;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.compare.MetaComparisonColumnGroup;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.compare.MetaComparisonTableGroup;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 30, 2010
 * Time: 8:14:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySqlMetaManipulate extends DefaultMetaManipulate
{
    public MySqlMetaManipulate(IDBLayer dbLayer)
    {
        super(dbLayer);
    }

    @Override
    protected String createAlterColumnQuery(MetaComparisonTableGroup tableGroup, MetaComparisonColumnGroup columnGroup)
    {
        MetaTable metaTable = tableGroup.getRequiredItem();
        MetaColumn metaColumn = columnGroup.getRequiredItem();

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(metaTable.getName());
        sb.append(" MODIFY ");
        sb.append(metaColumn.getName());
        sb.append(" ");
        if (metaColumn.getColumnType() == DBColumnType.CHAR
                || metaColumn.getColumnType() == DBColumnType.VARCHAR)
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
    public boolean Equals(IMetaItem iMetaItemA, IMetaItem iMetaItemB)
    {
        if (iMetaItemA.getItemType() == MetaItemType.PRIMARY_KEY
                && iMetaItemA.getItemType() == iMetaItemB.getItemType())
        {
            MetaPrimaryKey primaryKeyA = (MetaPrimaryKey) iMetaItemA;
            MetaPrimaryKey primaryKeyB = (MetaPrimaryKey) iMetaItemB;

            if (primaryKeyA.getColumnNames().size() != primaryKeyB.getColumnNames().size())
            {
                return false;
            }
            for (String columnA : primaryKeyA.getColumnNames())
            {
                boolean found = false;
                for (String columnB : primaryKeyB.getColumnNames())
                {
                    if (columnA.equalsIgnoreCase(columnB))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    return false;
                }
            }
            return true;
        }
        return super.Equals(iMetaItemA, iMetaItemB);
    }
}
