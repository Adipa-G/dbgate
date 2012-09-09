package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/9/12
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTable extends AbstractTable
{
    public DefaultTable(String tableName, UpdateStrategy updateStrategy, VerifyOnWriteStrategy verifyOnWriteStrategy,
                        DirtyCheckStrategy dirtyCheckStrategy)
    {
        super(tableName, updateStrategy, verifyOnWriteStrategy, dirtyCheckStrategy);
    }

    public DefaultTable(String tableName)
    {
        super(tableName);
    }
}
