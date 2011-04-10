package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures;

import dbgate.ermanagement.ReferentialRuleType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 3:18:01 PM
 */
public class MetaForeignKey extends AbstractMetaItem
{
    private String toTable;
    private ReferentialRuleType updateRule;
    private ReferentialRuleType deleteRule;
    private Collection<MetaForeignKeyColumnMapping> columnMappings;

    public MetaForeignKey()
    {
        itemType = MetaItemType.FOREIGN_KEY;
        columnMappings = new ArrayList<MetaForeignKeyColumnMapping>();
    }

    public String getToTable()
    {
        return toTable;
    }

    public void setToTable(String toTable)
    {
        this.toTable = toTable;
    }

    public ReferentialRuleType getUpdateRule()
    {
        return updateRule;
    }

    public void setUpdateRule(ReferentialRuleType updateRule)
    {
        this.updateRule = updateRule;
    }

    public ReferentialRuleType getDeleteRule()
    {
        return deleteRule;
    }

    public void setDeleteRule(ReferentialRuleType deleteRule)
    {
        this.deleteRule = deleteRule;
    }

    public Collection<MetaForeignKeyColumnMapping> getColumnMappings()
    {
        return columnMappings;
    }

    public void setColumnMappings(Collection<MetaForeignKeyColumnMapping> columnMappings)
    {
        this.columnMappings = columnMappings;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MetaForeignKey that = (MetaForeignKey) o;

        boolean foundMatch = false;
        for (MetaForeignKeyColumnMapping thisMapping : columnMappings)
        {
            for (MetaForeignKeyColumnMapping thatMapping : that.getColumnMappings())
            {
                if (thisMapping.equals(thatMapping))
                {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch)
            {
                return false;
            }
        }

        if (deleteRule != that.deleteRule) return false;
        if (!toTable.equalsIgnoreCase(that.toTable)) return false;
        if (updateRule != that.updateRule) return false;

        return true;
    }
}
