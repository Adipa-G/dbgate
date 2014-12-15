package dbgate.ermanagement.dbabstractionlayer;

import dbgate.DefaultTransactionFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 12:50:18 PM
 */
public class LayerFactory
{
    public static IDBLayer createLayer(int dbType)
    {
        switch (dbType)
        {
            case DefaultTransactionFactory.DB_ACCESS:
                return new AccessDBLayer();
            case DefaultTransactionFactory.DB_ORACLE:
                return new OracleDBLayer();
            case DefaultTransactionFactory.DB_MYSQL:
                return new MySqlDBLayer();
	        case DefaultTransactionFactory.DB_DERBY:
		        return new DerbyDBLayer();
            default:
                return new DefaultDBLayer();
        }
    }
}
