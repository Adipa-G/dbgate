package hibernate.entities.order;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by adipa_000 on 8/9/2018.
 */
public class Transaction
{
	private int transactionId;
	private String name;
	private Set<ItemTransaction> itemTransactions;

	public Transaction()
	{
		this.itemTransactions = new HashSet<>();
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

	public void setItemTransactions(Set<ItemTransaction> itemTransactions)
	{
		this.itemTransactions = itemTransactions;
	}
}
