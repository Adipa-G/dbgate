import dbgate.DbGatePerformanceCounter;
import hibernate.HibernatePerformanceCounter;

import java.io.Console;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by adipa_000 on 8/8/2018.
 */
public class MainProgram
{
	public static void main(String[] args)
	{
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(Level.INFO);

		for (Handler h : rootLogger.getHandlers()) {
			h.setLevel(Level.WARNING);
		}

		new DbGatePerformanceCounter(50).start(1);
		new HibernatePerformanceCounter(5000).start(1);
	}
}
