package dbgate.ermanagement;

import dbgate.DBColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jul 5, 2008
 * Time: 1:48:22 PM
 */
public class DefaultDBColumn extends AbstractDBColumn
{
    public DefaultDBColumn(String attributeName, DBColumnType type)
    {
        super(attributeName, type);
    }

    public DefaultDBColumn(String attributeName, DBColumnType type, boolean nullable)
    {
        super(attributeName, type, nullable);
    }

    public DefaultDBColumn(String attributeName, boolean key, DBColumnType type)
    {
        super(attributeName, key, type);
    }

    public DefaultDBColumn(String attributeName, boolean key, boolean nullable, DBColumnType type)
    {
        super(attributeName, key, nullable, type);
    }

    public DefaultDBColumn(String attributeName, String columnName, boolean key, DBColumnType type, boolean readFromSequence, ISequenceGenerator generator)
    {
        super(attributeName, columnName, key, type, readFromSequence, generator);
    }

    public DefaultDBColumn(String attributeName, String columnName, boolean key, boolean nullable, DBColumnType type, int size, boolean readFromSequence, ISequenceGenerator generator)
    {
        super(attributeName, columnName, key, nullable, type, size, readFromSequence, generator);
    }
}
