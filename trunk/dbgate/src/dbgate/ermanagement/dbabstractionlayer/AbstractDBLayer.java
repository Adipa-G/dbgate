package dbgate.ermanagement.dbabstractionlayer;

import dbgate.ITransaction;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.IDataManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.IMetaManipulate;
import dbgate.exceptions.migration.MetaDataException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 12:45:49 PM
 */
public abstract class AbstractDBLayer implements IDBLayer
{
    private IDataManipulate dataManipulate;
    private IMetaManipulate metaManipulate;

    public AbstractDBLayer()
    {
        dataManipulate = createDataManipulate();
    }

    protected abstract IDataManipulate createDataManipulate();

    protected abstract IMetaManipulate createMetaManipulate(ITransaction tx) throws MetaDataException;

    @Override
    public IDataManipulate getDataManipulate()
    {
        return dataManipulate;
    }

    @Override
    public IMetaManipulate getMetaManipulate(ITransaction tx) throws MetaDataException
    {
        if (metaManipulate == null)
        {
            metaManipulate = createMetaManipulate(tx);
        }
        return metaManipulate;
    }
}
