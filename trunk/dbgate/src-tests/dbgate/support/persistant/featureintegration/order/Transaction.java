package dbgate.support.persistant.featureintegration.order;

import dbgate.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 31, 2011
 * Time: 9:55:15 PM
 */
@TableInfo(tableName = "order_transaction")
public class Transaction extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int transactionId;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfo(name = "tx2item_tx"
                ,relatedObjectType = ItemTransaction.class
                ,updateRule = ReferentialRuleType.RESTRICT
                ,deleteRule = ReferentialRuleType.CASCADE
                , fieldMappings =  {@ForeignKeyFieldMapping(fromField = "transactionId", toField = "transactionId")})
    private Collection<ItemTransaction> itemTransactions;

    public Transaction()
    {
        itemTransactions = new ArrayList<ItemTransaction>();
    }

    public int getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(int transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Collection<ItemTransaction> getItemTransactions()
    {
        return itemTransactions;
    }

    public void setItemTransactions(Collection<ItemTransaction> itemTransactions)
    {
        this.itemTransactions = itemTransactions;
    }
}
