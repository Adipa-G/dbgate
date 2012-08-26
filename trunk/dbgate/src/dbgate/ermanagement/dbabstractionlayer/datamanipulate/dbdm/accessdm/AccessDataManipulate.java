package dbgate.ermanagement.dbabstractionlayer.datamanipulate.dbdm.accessdm;

import dbgate.ColumnType;
import dbgate.IColumn;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.AbstractDataManipulate;
import dbgate.exceptions.common.ReadFromResultSetException;

import java.sql.ResultSet;

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
    public Object readFromResultSet(ResultSet rs, IColumn dbColumn) throws ReadFromResultSetException
    {
        Object result = super.readFromResultSet(rs, dbColumn);
        if (result != null
                && dbColumn.getColumnType() == ColumnType.VARCHAR)
        {
            return result.toString().replaceAll("\u0000","");
        }
        return result;
    }
}
