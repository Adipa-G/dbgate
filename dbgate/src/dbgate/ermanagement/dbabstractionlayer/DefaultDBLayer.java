package dbgate.ermanagement.dbabstractionlayer;

import dbgate.ITransaction;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.IDataManipulate;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.dbdm.defaultdm.DefaultDataManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.IMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.defaultmm.DefaultMetaManipulate;
import dbgate.exceptions.migration.MetaDataException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 10:57:00 AM
 */
public class DefaultDBLayer extends AbstractDBLayer
{
    public DefaultDBLayer()
    {
    }

    @Override
    protected IDataManipulate createDataManipulate()
    {
        return new DefaultDataManipulate(this);
    }

    @Override
    protected IMetaManipulate createMetaManipulate(ITransaction tx) throws MetaDataException
    {
        DefaultMetaManipulate defaultMetaManipulate = new DefaultMetaManipulate(this);
        defaultMetaManipulate.initialize(tx);
        return defaultMetaManipulate;
    }
}
