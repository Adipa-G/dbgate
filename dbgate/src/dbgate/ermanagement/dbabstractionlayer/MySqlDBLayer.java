package dbgate.ermanagement.dbabstractionlayer;

import dbgate.ermanagement.dbabstractionlayer.datamanipulate.IDataManipulate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 30, 2010
 * Time: 8:12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySqlDBLayer  extends DefaultDBLayer
{
    @Override
    protected IDataManipulate createDataManipulate()
    {
        return super.createDataManipulate();
    }
}
