package hibernate;

import dbgate.entities.order.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaQuery;
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

	private void insertTet(List<Object> entities) throws Exception
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

		Logger.getLogger(getClass().getName()).warning(String.format("DBGate thread insert speed %s entities/second", speed));
	}

	private void updateTet(List<Object> entities) throws Exception
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

		Logger.getLogger(getClass().getName()).warning(String.format("DBGate thread update speed %s entities/second", speed));
	}

	private void deleteTet(List<Object> entities) throws Exception
	{
		long start = new Date().getTime();
		Session session = factory.openSession();
		org.hibernate.Transaction transaction = session.beginTransaction();

		for (int i = 0; i < entities.size(); i++)
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

		Logger.getLogger(getClass().getName()).warning(String.format("DBGate thread delete speed %s entities/second", speed));
	}
}
