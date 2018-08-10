package hibernate.entities.order;


import java.io.Serializable;

/**
 * Date: Mar 31, 2011
 * Time: 9:59:13 PM
 */
public class ItemTransactionCharge implements Serializable
{
    private int transactionId;
    private int indexNo;
    private int chargeIndex;
    private String chargeCode;
    private ItemTransaction itemTransaction;

    public ItemTransactionCharge()
    {
    }

    public ItemTransactionCharge(ItemTransaction itemTransaction)
    {
        this.itemTransaction = itemTransaction;
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

    public ItemTransaction getItemTransaction()
    {
        return itemTransaction;
    }

    public void setItemTransaction(ItemTransaction itemTransaction)
    {
        this.itemTransaction = itemTransaction;
    }
}
