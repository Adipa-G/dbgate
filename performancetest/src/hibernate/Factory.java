package hibernate;

import hibernate.entities.order.ItemTransaction;
import hibernate.entities.order.ItemTransactionCharge;
import hibernate.entities.order.Transaction;
import hibernate.entities.product.Item;
import hibernate.entities.product.Product;
import hibernate.entities.product.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by adipa_000 on 8/8/2018.
 */
public class Factory
{
	private final Random random = new Random();

	public List<Object> generate(int seed, int txCount, int productsOrServicesPerTx)
	{
		List<Object> list = new ArrayList<Object>();
		List<Integer> productIds = new ArrayList<Integer>();
		List<Integer> serviceIds = new ArrayList<Integer>();
		int productOrCount = txCount * productsOrServicesPerTx;

		for (int i = 0; i < productOrCount; i++)
		{
			Product product = new Product();
			product.setItemId(seed + 2 * i);
			product.setName("Product - "+ i +"");
			product.setUnitPrice(54 + i);
			product.setBulkUnitPrice(104d + i);
			productIds.add(product.getItemId());
			list.add(product);

			Service service = new Service();
			service.setItemId(seed + 2 * i + 1);
			service.setName("Service - " + i + "");
			service.setHourlyRate(10 + i);
			serviceIds.add(service.getItemId());
			list.add(service);
		}

		for (int i = 0; i < txCount; i++)
		{
			Transaction transaction = new Transaction();
			transaction.setTransactionId(seed + i);
			transaction.setName("TRS-000"+ i );

			int productsCount = random.nextInt(productsOrServicesPerTx) + 1;
			for (int j = 0; j < productsCount; j++)
			{
				int productId = productIds.get(random.nextInt(productIds.size()));
				Product product = null;
				for (Object entity : list)
				{
					if (entity instanceof Product && ((Item)entity).getItemId() == productId){
						product = (Product)entity;
					}
				}

				ItemTransaction productTransaction = new ItemTransaction(transaction);
				productTransaction.setTransactionId(transaction.getTransactionId());
				productTransaction.setIndexNo(j);
				productTransaction.setItem(product);
				transaction.getItemTransactions().add(productTransaction);

				ItemTransactionCharge productTransactionCharge = new ItemTransactionCharge(productTransaction);
				productTransactionCharge.setTransactionId(transaction.getTransactionId());
				productTransactionCharge.setIndexNo(productTransaction.getIndexNo());
				productTransactionCharge.setChargeIndex(0);

				productTransactionCharge.setChargeCode("Product-Sell-Code "+ i+" " + j);
				productTransaction.getItemTransactionCharges().add(productTransactionCharge);
			}

			int servicesCount = random.nextInt(productsOrServicesPerTx);
			for (int j = 0; j < servicesCount; j++)
			{
				int serviceId = serviceIds.get(random.nextInt(serviceIds.size()));
				Service service = null;
				for (Object entity : list)
				{
					if (entity instanceof Service && ((Item)entity).getItemId() == serviceId){
						service = (Service)entity;
					}
				}

				ItemTransaction serviceTransaction = new ItemTransaction(transaction);
				serviceTransaction.setTransactionId(transaction.getTransactionId());
				serviceTransaction.setIndexNo(productsCount + j + 1);
				serviceTransaction.setItem(service);
				transaction.getItemTransactions().add(serviceTransaction);

				ItemTransactionCharge serviceTransactionCharge = new ItemTransactionCharge(serviceTransaction);
				serviceTransactionCharge.setTransactionId(transaction.getTransactionId());
				serviceTransactionCharge.setIndexNo(serviceTransaction.getIndexNo());
				serviceTransactionCharge.setChargeIndex(0);

				serviceTransactionCharge.setChargeCode("Service-Sell-Code " + i + " " + j);
				serviceTransaction.getItemTransactionCharges().add(serviceTransactionCharge);
			}
			list.add(transaction);
		}
		return list;
	}

	public void update(List<Object> entities)
	{
		for (Object entity : entities)
		{
			if (entity instanceof Product)
			{
				Product product = (Product)entity;
				product.setName("Upd " + product.getName());
				product.setUnitPrice(product.getUnitPrice() + 5);
				product.setBulkUnitPrice(product.getBulkUnitPrice() + 2);
			}
			else if (entity instanceof Service)
			{
				Service service = (Service )entity;
				service.setName("Upd " + service.getName());
				service.setHourlyRate(service.getHourlyRate() + 2);
			}
			else if (entity instanceof Transaction)
			{
				Transaction transaction = (Transaction)entity;
				transaction.setName("Upd " + transaction.getName());

				ItemTransaction prev = null;
				for (ItemTransaction itemTransaction : transaction.getItemTransactions())
				{
					if (prev != null)
					{
						itemTransaction.setItem(prev.getItem());
					}

					for (ItemTransactionCharge charge : itemTransaction.getItemTransactionCharges())
					{
						charge.setChargeCode("Upd " + charge.getChargeCode());
					}

					prev = itemTransaction;
				}
			}
		}
	}
}
