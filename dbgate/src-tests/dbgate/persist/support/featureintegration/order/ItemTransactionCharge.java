package dbgate.persist.support.featureintegration.order;

import dbgate.*;

/**
 * Date: Mar 31, 2011
 * Time: 9:59:13 PM
 */
@TableInfo(tableName = "order_item_transaction_charge")
public class ItemTransactionCharge  extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int transactionId;
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int indexNo;
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int chargeIndex;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String chargeCode;
    @ForeignKeyInfo(name = "item_tx_charge2tx_rev",
            relatedObjectType = Transaction.class,
            reverseRelation = true,
            fieldMappings =  {@ForeignKeyFieldMapping(fromField = "transactionId", toField = "transactionId")})
    private Transaction transaction;
    @ForeignKeyInfo(name = "item_tx_charge2tx_item_rev",
            relatedObjectType = ItemTransaction.class,
            reverseRelation = true,
            fieldMappings =  {@ForeignKeyFieldMapping(fromField = "transactionId", toField = "transactionId"),
                              @ForeignKeyFieldMapping(fromField = "indexNo", toField = "indexNo")})
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
