package hibernate;

import hibernate.entities.order.Transaction;
import hibernate.entities.product.Product;
import hibernate.entities.product.Service;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.Console;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by adipa_000 on 8/9/2018.
 */
public class HibernatePerformanceCounter
{
	private int perThread = 100;
	private static SessionFactory factory;

	public HibernatePerformanceCounter(int perThread)
	{
		this.perThread = perThread;

		try
		{
			factory = new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex)
		{
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception during initialisation.", ex);
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
			List<Object> entities = factory.generate(seed, perThread, 10);
			insertTest(entities);
			queryTest(entities);

			factory.update(entities);
			updateTest(entities);
			deleteTest(entities);
		}
		catch (Exception ex)
		{
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"Exception during persisting.", ex);
		}
	}

	private void insertTest(List<Object> entities) throws Exception
	{
		long start = new Date().getTime();
		Session session = factory.openSession();
		org.hibernate.Transaction transaction = session.beginTransaction();

		for (int i = 0; i < entities.size(); i++)
		{
			Object entity = entities.get(i);
			session.save(entity);

			if (i % 100 == 0){
				session.flush();
				transaction.commit();
				session.close();

				session = factory.openSession();
				transaction = session.beginTransaction();
			}
		}

		session.flush();
		transaction.commit();
		session.close();


		long end = new Date().getTime();
		long speed = entities.size() * 1000 / (end - start);

		Logger.getLogger(getClass().getName()).warning(String.format("Hibernate thread insert speed %s entities/second", speed));
	}

	private void queryTest(List<Object> entities) throws Exception
	{
		long start = new Date().getTime();
		Session session = factory.openSession();
		org.hibernate.Transaction transaction = session.beginTransaction();

		for (int i = 0; i < entities.size(); i++)
		{
			Object entity = entities.get(i);
			Class entityType = entity.getClass();

			if (entityType == Product.class)
			{
				Product product = (Product)entity;
				Query query = session.createQuery("FROM Product WHERE itemId = :itemId");
				query.setParameter("itemId",product.getItemId());

				Product loadedPrd = (Product)query.uniqueResult();
			}
			else if (entityType == Service.class)
			{
				Service service = (Service)entity;
				Query query = session.createQuery("FROM Service WHERE itemId = :itemId");
				query.setParameter("itemId",service.getItemId());

				Service loadedSrv = (Service)query.uniqueResult();
			}
			else if (entityType == Transaction.class)
			{
				Transaction tx = (Transaction)entity;
				Query query = session.createQuery("FROM Transaction WHERE transactionId = :transactionId");
				query.setParameter("transactionId",tx.getTransactionId());

				Transaction loadedTx = (Transaction)query.uniqueResult();
			}

			if (i % 100 == 0){
				session.flush();
				transaction.commit();
				session.close();

				session = factory.openSession();
				transaction = session.beginTransaction();
			}
		}

		session.flush();
		transaction.commit();
		session.close();

		long end = new Date().getTime();
		long speed = entities.size() * 1000 / (end - start);

		Logger.getLogger(getClass().getName()).warning(String.format("Hibernate thread query speed %s entities/second", speed));
	}

	private void updateTest(List<Object> entities) throws Exception
	{
		long start = new Date().getTime();
		Session session = factory.openSession();
		org.hibernate.Transaction transaction = session.beginTransaction();

		for (int i = 0; i < entities.size(); i++)
		{
			Object entity = entities.get(i);
			session.update(entity);

			if (i % 100 == 0){
				session.flush();
				transaction.commit();
				session.close();

				session = factory.openSession();
				transaction = session.beginTransaction();
			}
		}

		session.flush();
		transaction.commit();
		session.close();

		long end = new Date().getTime();
		long speed = entities.size() * 1000 / (end - start);

		Logger.getLogger(getClass().getName()).warning(String.format("Hibernate thread update speed %s entities/second", speed));
	}

	private void deleteTest(List<Object> entities) throws Exception
	{
		long start = new Date().getTime();
		Session session = factory.openSession();
		org.hibernate.Transaction transaction = session.beginTransaction();

		for (int i = entities.size() - 1 ; i >= 0; i--)
		{
			Object entity = entities.get(i);
			session.delete(entity);

			if (i % 100 == 0){
				session.flush();
				transaction.commit();
				session.close();

				session = factory.openSession();
				transaction = session.beginTransaction();
			}
		}

		session.flush();
		transaction.commit();
		session.close();

		long end = new Date().getTime();
		long speed = entities.size() * 1000 / (end - start);

		Logger.getLogger(getClass().getName()).warning(String.format("Hibernate thread delete speed %s entities/second", speed));
	}
}
