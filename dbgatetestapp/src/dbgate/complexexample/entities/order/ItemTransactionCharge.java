package dbgate.complexexample.entities.order;

import dbgate.DBColumnType;
import dbgate.ermanagement.*;

/**
 * Date: Mar 31, 2011
 * Time: 9:59:13 PM
 */
@DBTableInfo(tableName = "order_item_transaction_charge")
public class ItemTransactionCharge  extends DefaultServerDBClass
{
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int transactionId;
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int indexNo;
    @DBColumnInfo(columnType = DBColumnType.INTEGER,key = true)
    private int chargeIndex;
    @DBColumnInfo(columnType = DBColumnType.VARCHAR)
    private String chargeCode;
    @ForeignKeyInfo(name = "item_tx_charge2tx_rev"
                ,relatedObjectType = Transaction.class
                ,reverseRelation = true
                ,columnMappings =  {@ForeignKeyColumnMapping(fromField = "transactionId",toField = "transactionId")})
    private Transaction transaction;
    @ForeignKeyInfo(name = "item_tx_charge2tx_item_rev"
                ,relatedObjectType = ItemTransaction.class
                ,reverseRelation = true
                ,columnMappings =  {@ForeignKeyColumnMapping(fromField = "transactionId",toField = "transactionId")
                    ,@ForeignKeyColumnMapping(fromField = "indexNo",toField = "indexNo")})
    private ItemTransaction itemTransaction;

    public ItemTransactionCharge()
    {
    }

    public ItemTransactionCharge(ItemTransaction itemTransaction)
    {
        this.itemTransaction = itemTransaction;
        this.transaction = itemTransaction.getTransaction();
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

    public int getChargeIndex()
    {
        return chargeIndex;
    }

    public void setChargeIndex(int chargeIndex)
    {
        this.chargeIndex = chargeIndex;
    }

    public String getChargeCode()
    {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode)
    {
        this.chargeCode = chargeCode;
    }

    public Transaction getTransaction()
    {
        return transaction;
    }

    public void setTransaction(Transaction transaction)
    {
        this.transaction = transaction;
    }

    public ItemTransaction getItemTransaction()
    {
        return itemTransaction;
    }

    public void setItemTransaction(ItemTransaction itemTransaction)
    {
        this.itemTransaction = itemTransaction;
    }
}
