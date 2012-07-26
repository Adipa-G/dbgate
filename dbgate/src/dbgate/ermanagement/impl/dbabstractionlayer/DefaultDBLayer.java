package dbgate.ermanagement.impl.dbabstractionlayer;

import dbgate.ermanagement.exceptions.MetaDataException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.DefaultDataManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.IDataManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.DefaultMetaManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.IMetaManipulate;

import java.sql.Connection;

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
    protected IMetaManipulate createMetaManipulate(Connection con) throws MetaDataException
    {
        DefaultMetaManipulate defaultMetaManipulate = new DefaultMetaManipulate(this);
        defaultMetaManipulate.initialize(con);
        return defaultMetaManipulate;
    }
}