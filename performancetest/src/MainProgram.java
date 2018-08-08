import dbgate.DbGatePerformanceCounter;

/**
 * Created by adipa_000 on 8/8/2018.
 */
public class MainProgram
{
	public static void main(String[] args)
	{
		new DbGatePerformanceCounter().start(10);
	}
}
