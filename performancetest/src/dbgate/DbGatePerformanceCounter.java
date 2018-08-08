package dbgate;

import dbgate.entities.order.ItemTransaction;
import dbgate.entities.order.ItemTransactionCharge;
import dbgate.entities.order.Transaction;
import dbgate.entities.product.Product;
import dbgate.entities.product.Service;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.common.TransactionCloseFailedException;
import dbgate.exceptions.common.TransactionCommitFailedException;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by adipa_000 on 8/8/2018.
 */
public class DbGatePerformanceCounter
{
	private int perThread = 100;
	private DefaultTransactionFactory transactionFactory;

	public DbGatePerformanceCounter()
	{
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
		try
		{
			List<IEntity> items = new Factory().Generate(seed,perThread, 10);
			ITransaction tx = transactionFactory.createTransaction();
			for (int i = 0; i < items.size(); i++)
			{
				items.get(i).persist(tx);
				if (i % 100 == 0)
				{
					tx.commit();
					tx.close();
					tx = transactionFactory.createTransaction();
				}
			}
			tx.commit();
			tx.close();
		}
		catch (Exception ex){
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"Exception during persisting.", ex);
		}
	}
}
