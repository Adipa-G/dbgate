package dbgate.ermanagement.dbabstractionlayer;

import dbgate.ITransaction;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.IDataManipulate;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.dbdm.sqlserverdm.SqlServerDataManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.IMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.defaultmm.DefaultMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.sqlservermm.SqlServerMetaManipulate;
import dbgate.exceptions.migration.MetaDataException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 12:47:52 PM
 */
public class SqlServerDBLayer extends AbstractDBLayer
{
    public SqlServerDBLayer()
    {
    }

    @Override
    protected IDataManipulate createDataManipulate()
    {
        return new SqlServerDataManipulate(this);
    }

    @Override
    protected IMetaManipulate createMetaManipulate(ITransaction tx) throws MetaDataException
    {
        SqlServerMetaManipulate sqlServerMetaManipulate = new SqlServerMetaManipulate(this);
        sqlServerMetaManipulate.initialize(tx);
        return sqlServerMetaManipulate;
    }
}
