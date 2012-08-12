package dbgate.ermanagement.dbabstractionlayer;

import dbgate.exceptions.migration.MetaDataException;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.IDataManipulate;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.dbdm.accessdm.AccessDataManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.IMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.defaultmm.DefaultMetaManipulate;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 12:47:52 PM
 */
public class AccessDBLayer extends AbstractDBLayer
{
    public AccessDBLayer()
    {
    }

    @Override
    protected IDataManipulate createDataManipulate()
    {
        return new AccessDataManipulate(this);
    }

    @Override
    protected IMetaManipulate createMetaManipulate(Connection con) throws MetaDataException
    {
        DefaultMetaManipulate defaultMetaManipulate = new DefaultMetaManipulate(this);
        defaultMetaManipulate.initialize(con);
        return defaultMetaManipulate;
    }
}
