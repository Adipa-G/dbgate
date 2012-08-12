package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.dbdm.accessdm;

import dbgate.ColumnType;
import dbgate.ermanagement.IColumn;
import dbgate.ermanagement.exceptions.common.ReadFromResultSetException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.AbstractDataManipulate;

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
