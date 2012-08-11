package dbgate.ermanagement.impl.dbabstractionlayer;

import dbgate.ermanagement.exceptions.migration.MetaDataException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.IDataManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.dbdm.oracledm.OracleDataManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.IMetaManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.dbmm.defaultmm.DefaultMetaManipulate;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 1:14:41 PM
 */
public class OracleDBLayer extends AbstractDBLayer
{
    public OracleDBLayer()
    {
    }

    @Override
    protected IDataManipulate createDataManipulate()
    {
        return new OracleDataManipulate(this);
    }

    @Override
    protected IMetaManipulate createMetaManipulate(Connection con) throws MetaDataException
    {
         DefaultMetaManipulate defaultMetaManipulate = new DefaultMetaManipulate(this);
        defaultMetaManipulate.initialize(con);
        return defaultMetaManipulate;
    }
}
