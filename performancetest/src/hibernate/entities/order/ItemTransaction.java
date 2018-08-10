package hibernate.entities.order;

import hibernate.entities.product.Item;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ItemTransaction implements Serializable
{
    private int transactionId;
    private int indexNo;
    private Item item;
    private Transaction transaction;
    private Set<ItemTransactionCharge> itemTransactionCharges;

    public ItemTransaction()
    {
    }

    public ItemTransaction(Transaction transaction)
    {
        this.transaction = transaction;
        itemTransactionCharges = new HashSet<ItemTransactionCharge>();
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

    public void setItemTransactionCharges(Set<ItemTransactionCharge> itemTransactionCharges)
    {
        this.itemTransactionCharges = itemTransactionCharges;
    }
}
