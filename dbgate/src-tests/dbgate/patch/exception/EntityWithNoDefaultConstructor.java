package dbgate.patch.exception;

import dbgate.*;

import java.util.Collection;
import java.util.Map;

/**
 * User: Adipa
 * Date: 11/25/12
 * Time: 12:52 PM
 */
@TableInfo(tableName = "exception_test_root")
public class EntityWithNoDefaultConstructor extends AbstractManagedEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int idCol;

    public EntityWithNoDefaultConstructor(String id)
    {
    }

    @Override
    public Map<Class, ITable> getTableInfo()
    {
        return null;
    }

    @Override
    public Map<Class, Collection<IField>> getFieldInfo()
    {
        return null;
    }

    public int getIdCol() throws Exception
    {
        throw new Exception("can't get");
    }

    public void setIdCol(int idCol) throws Exception
    {
        throw new Exception("can't set");
    }
}
