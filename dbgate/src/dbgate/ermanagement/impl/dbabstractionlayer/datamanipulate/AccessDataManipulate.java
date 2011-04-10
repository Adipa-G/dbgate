package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.DBColumnType;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 6:49:01 PM
 */
public class AccessDataManipulate extends AbstractDataManipulate
{
    public AccessDataManipulate(IDBLayer dbLayer)
    {
        super(dbLayer);
    }

    @Override
    public Object readFromResultSet(ResultSet rs, IDBColumn dbColumn) throws SQLException
    {
        Object result = super.readFromResultSet(rs, dbColumn);
        if (result != null
                && dbColumn.getColumnType() == DBColumnType.VARCHAR)
        {
            return result.toString().replaceAll("\u0000","");
        }
        return result;
    }
}
