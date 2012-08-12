package dbgate.ermanagement;

import dbgate.ColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jul 5, 2008
 * Time: 1:48:22 PM
 */
public class DefaultColumn extends AbstractColumn
{
    public DefaultColumn(String attributeName, ColumnType type)
    {
        super(attributeName, type);
    }

    public DefaultColumn(String attributeName, ColumnType type, boolean nullable)
    {
        super(attributeName, type, nullable);
    }

    public DefaultColumn(String attributeName, boolean key, ColumnType type)
    {
        super(attributeName, key, type);
    }

    public DefaultColumn(String attributeName, boolean key, boolean nullable, ColumnType type)
    {
        super(attributeName, key, nullable, type);
    }

    public DefaultColumn(String attributeName, String columnName, boolean key, ColumnType type,
                         boolean readFromSequence, ISequenceGenerator generator)
    {
        super(attributeName, columnName, key, type, readFromSequence, generator);
    }

    public DefaultColumn(String attributeName, String columnName, boolean key, boolean nullable, ColumnType type,
                         int size, boolean readFromSequence, ISequenceGenerator generator)
    {
        super(attributeName, columnName, key, nullable, type, size, readFromSequence, generator);
    }
}
