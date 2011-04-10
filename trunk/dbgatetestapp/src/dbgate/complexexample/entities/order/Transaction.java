package dbgate.complexexample.entities.order;

import dbgate.DBColumnType;
import dbgate.ermanagement.*;
import dbgate.one2oneexample.entities.One2OneChildEntityA;
import dbgate.one2oneexample.entities.One2OneChildEntityB;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 31, 2011
 * Time: 9:55:15 PM
 */
@DBTableInfo(tableName = "order_transaction")
public class Transaction extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int transactionId;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String name;
    @ForeignKeyInfo(name = "tx2item_tx"
                ,relatedObjectType = ItemTransaction.class
                ,updateRule = ReferentialRuleType.RESTRICT
                ,deleteRule = ReferentialRuleType.CASCADE
                ,columnMappings =  {@ForeignKeyColumnMapping(fromField = "transactionId",toField = "transactionId")})
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
