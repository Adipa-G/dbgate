package dbgate;

import dbgate.entities.order.ItemTransaction;
import dbgate.entities.order.ItemTransactionCharge;
import dbgate.entities.order.Transaction;
import dbgate.entities.product.Product;
import dbgate.entities.product.Service;
import dbgate.ermanagement.query.SelectionQuery;
import org.hibernate.sql.QuerySelect;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by adipa_000 on 8/8/2018.
 */
public class DbGatePerformanceCounter
{
	private int perThread = 100;
	private DefaultTransactionFactory transactionFactory;

	public DbGatePerformanceCounter(int perThread)
	{
		this.perThread = perThread;

		try
		{
			if (transactionFactory == null)
			{
				Logger.getLogger(getClass().getName()).info("Connecting to sql server for performance testing");

				transactionFactory = new DefaultTransactionFactory(() -> {
					try
					{
						Logger.getLogger(getClass().getName()).info("Starting in-memory database for unit tests");
						Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
						return DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=DbGateJ","sa","sa");
					}
					catch (Exception ex)
					{
						Logger.getLogger(getClass().getName()).severe("Unable to connect to the database.");
						return null;
					}
				}, DefaultTransactionFactory.DB_SQLSERVER);

				ITransaction tx = transactionFactory.createTransaction();
				List<Class> entityTypes = new ArrayList<Class>();
				entityTypes.add(Product.class);
				entityTypes.add(Service.class);
				entityTypes.add(Transaction.class);
				entityTypes.add(ItemTransaction.class);
				entityTypes.add(ItemTransactionCharge.class);
				tx.getDbGate().patchDataBase(tx, entityTypes, true);
				tx.commit();
			}
		}
		catch (Exception ex)
		{
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"Exception during initialisation.", ex);
		}
	}

	public void start(int threads)
	{
		List<Thread> threadList = new ArrayList<>();
		for (int i = 0; i < threads; i++)
		{
			int copy = i;
			Thread thread = new Thread(() -> doInThread(copy * 100000));
			thread.start();
			threadList.add(thread);
		}

		try
		{
			for (Thread thread : threadList)
			{
				thread.join();
			}
		}
		catch (Exception ex){
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"Exception during thread execution.", ex);
		}
	}

	private void doInThread(int seed)
	{
		Factory factory = new Factory();

		try
		{
			List<IEntity> entities = factory.generate(seed, perThread, 10);
			insertTet(entities);
			//queryTet(entities);

			factory.update(entities);
			updateTet(entities);
			deleteTet(entities);
		}
		catch (Exception ex)
		{
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"Exception during persisting.", ex);
		}
	}

	private void insertTet(List<IEntity> entities) throws Exception
	{
		long start = new Date().getTime();
		ITransaction transaction = transactionFactory.createTransaction();

		for (int i = 0; i < entities.size(); i++)
		{
			IEntity entity = entities.get(i);
			entity.persist(transaction);
			if (i % 100 == 0){
				transaction.commit();
				transaction.close();
				transaction = transactionFactory.createTransaction();
			}
		}
		transaction.commit();
		transaction.close();

		long end = new Date().getTime();
		long speed = entities.size() * 1000 / (end - start);

		Logger.getLogger(getClass().getName()).warning(String.format("DBGate thread insert speed %s entities/second", speed));
	}

	private void updateTet(List<IEntity> entities) throws Exception
	{
		long start = new Date().getTime();
		ITransaction transaction = transactionFactory.createTransaction();

		for (int i = 0; i < entities.size(); i++)
		{
			IEntity entity = entities.get(i);
			entity.setStatus(EntityStatus.MODIFIED);
			entity.persist(transaction);
			if (i % 100 == 0){
				transaction.commit();
				transaction.close();
				transaction = transactionFactory.createTransaction();
			}
		}
		transaction.commit();
		transaction.close();

		long end = new Date().getTime();
		long speed = entities.size() * 1000 / (end - start);

		Logger.getLogger(getClass().getName()).warning(String.format("DBGate thread update speed %s entities/second", speed));
	}

	private void queryTet(List<IEntity> entities) throws Exception
	{
		long start = new Date().getTime();
		ITransaction transaction = transactionFactory.createTransaction();

		for (int i = 0; i < entities.size(); i++)
		{
			IEntity entity = entities.get(i);
			Object loaded = new SelectionQuery()
					.from(QueryFrom.type(entity.getClass()))
					.select(QuerySelection.type(entity.getClass()))
					.toList(transaction).iterator().next();

			if (i % 100 == 0){
				transaction.close();
				transaction = transactionFactory.createTransaction();
			}
		}
		transaction.close();

		long end = new Date().getTime();
		long speed = entities.size() * 1000 / (end - start);

		Logger.getLogger(getClass().getName()).warning(String.format("DBGate thread query speed %s entities/second", speed));
	}

	private void deleteTet(List<IEntity> entities) throws Exception
	{
		long start = new Date().getTime();
		ITransaction transaction = transactionFactory.createTransaction();

		for (int i = 0; i < entities.size(); i++)
		{
			IEntity entity = entities.get(i);
			entity.setStatus(EntityStatus.DELETED);
			entity.persist(transaction);
			if (i % 100 == 0){
				transaction.commit();
				transaction.close();
				transaction = transactionFactory.createTransaction();
			}
		}
		transaction.commit();
		transaction.close();

		long end = new Date().getTime();
		long speed = entities.size() * 1000 / (end - start);

		Logger.getLogger(getClass().getName()).warning(String.format("DBGate thread delete speed %s entities/second", speed));
	}
}
