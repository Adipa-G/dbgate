package dbgate.ermanagement.dbabstractionlayer.datamanipulate.dbdm.sqlserverdm;

import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.AbstractDataManipulate;

/**
 * Created by adipa_000 on 8/7/2018.
 */
public class SqlServerDataManipulate extends AbstractDataManipulate
{
	public SqlServerDataManipulate(IDBLayer dbLayer)
	{
		super(dbLayer);
	}

//	@Override
//	protected String fixUpQuery(String query)
//	{
//		StringBuilder sb = new StringBuilder();
//		String[] tokens = query.split("\\?");
//		for (int i = 0; i < tokens.length; i++)
//		{
//			String token = tokens[i];
//			if (i > 0)
//			{
//				sb.append("@{").append(i).append("}");
//			}
//			sb.append(token);
//		}
//		return sb.toString();
//	}
}
