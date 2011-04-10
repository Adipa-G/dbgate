package dbgate.ermanagement.impl.dbabstractionlayer;

import dbgate.ermanagement.exceptions.MetaDataException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.IDataManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.IMetaManipulate;

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
