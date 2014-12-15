package dbgate.ermanagement.dbabstractionlayer;

import dbgate.ITransaction;
import dbgate.ermanagement.dbabstractionlayer.DefaultDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.IDataManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.IMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.derbymm.DerbyMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.mysqlmm.MySqlMetaManipulate;
import dbgate.exceptions.migration.MetaDataException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 30, 2010
 * Time: 8:12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class DerbyDBLayer extends DefaultDBLayer
{
    @Override
    protected IDataManipulate createDataManipulate()
    {
        return super.createDataManipulate();
    }

	@Override
	protected IMetaManipulate createMetaManipulate(ITransaction tx) throws MetaDataException
	{
		DerbyMetaManipulate derbyMetaManipulate = new DerbyMetaManipulate(this);
		derbyMetaManipulate.initialize(tx);
		return derbyMetaManipulate;
	}
}
