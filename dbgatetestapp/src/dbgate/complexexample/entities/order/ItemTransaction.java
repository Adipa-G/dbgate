package dbgate.complexexample.entities.order;

import dbgate.DBColumnType;
import dbgate.complexexample.entities.product.Item;
import dbgate.complexexample.entities.product.Product;
import dbgate.complexexample.entities.product.Service;
import dbgate.ermanagement.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 31, 2011
 * Time: 9:59:13 PM
 */
@DBTableInfo(tableName = "order_item_transaction")
public class ItemTransaction  extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int transactionId;
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int indexNo;
    @DBColumnInfo(columnType = DBColumnType.INTEGER)
    private int itemId;
    @ForeignKeyInfoList(infoList = {
    @ForeignKeyInfo(name = "item_tx2product"
                ,relatedObjectType = Product.class
                ,updateRule = ReferentialRuleType.RESTRICT
                ,deleteRule = ReferentialRuleType.CASCADE
                ,nonIdentifyingRelation = true
                ,columnMappings =  {@ForeignKeyColumnMapping(fromField = "itemId",toField = "itemId")})
     ,
    @ForeignKeyInfo(name = "item_tx2service"
                ,relatedObjectType = Service.class
                ,updateRule = ReferentialRuleType.RESTRICT
                ,deleteRule = ReferentialRuleType.CASCADE
                ,nonIdentifyingRelation = true
                ,columnMappings =  {@ForeignKeyColumnMapping(fromField = "itemId",toField = "itemId")})
    })
    private Item item;
    @ForeignKeyInfo(name = "item_tx2tx_rev"
                ,relatedObjectType = Transaction.class
                ,reverseRelation = true
                ,columnMappings =  {@ForeignKeyColumnMapping(fromField = "transactionId",toField = "transactionId")})
    private Transaction transaction;
    @ForeignKeyInfo(name = "tx2item_tx_charge"
                ,relatedObjectType = ItemTransactionCharge.class
                ,updateRule = ReferentialRuleType.RESTRICT
                ,deleteRule = ReferentialRuleType.CASCADE
                ,columnMappings =  {@ForeignKeyColumnMapping(fromField = "transactionId",toField = "transactionId")
                                    ,@ForeignKeyColumnMapping(fromField = "indexNo",toField = "indexNo")})
    private Collection<ItemTransactionCharge> itemTransactionCharges;

    public ItemTransaction()
    {
    }

    public ItemTransaction(Transaction transaction)
    {
        this.transaction = transaction;
        itemTransactionCharges = new ArrayList<ItemTransactionCharge>();
    }

    public int getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(int transactionId)
    {
        this.transactionId = transactionId;
    }

    public int getIndexNo()
    {
        return indexNo;
    }

    public void setIndexNo(int indexNo)
    {
        this.indexNo = indexNo;
    }

    public int getItemId()
    {
        return itemId;
    }

    public void setItemId(int itemId)
    {
        this.itemId = itemId;
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public Transaction getTransaction()
    {
        return transaction;
    }

    public void setTransaction(Transaction transaction)
    {
        this.transaction = transaction;
    }

    public Collection<ItemTransactionCharge> getItemTransactionCharges()
    {
        return itemTransactionCharges;
    }

    public void setItemTransactionCharges(Collection<ItemTransactionCharge> itemTransactionCharges)
    {
        this.itemTransactionCharges = itemTransactionCharges;
    }
}
