package dbgate.ermanagement.dbabstractionlayer;

import dbgate.exceptions.migration.MetaDataException;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.IDataManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.IMetaManipulate;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 12:42:13 PM
 */
public interface IDBLayer
{
    IDataManipulate getDataManipulate();

    IMetaManipulate getMetaManipulate(Connection con) throws MetaDataException;
}
