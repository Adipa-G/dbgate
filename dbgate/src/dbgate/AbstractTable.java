package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/9/12
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTable implements ITable
{
    private String tableName;
    private UpdateStrategy updateStrategy;
    private VerifyOnWriteStrategy verifyOnWriteStrategy;
    private DirtyCheckStrategy dirtyCheckStrategy;

    protected AbstractTable(String tableName, UpdateStrategy updateStrategy,
                            VerifyOnWriteStrategy verifyOnWriteStrategy, DirtyCheckStrategy dirtyCheckStrategy)
    {
        this.tableName = tableName;
        this.updateStrategy = updateStrategy;
        this.verifyOnWriteStrategy = verifyOnWriteStrategy;
        this.dirtyCheckStrategy = dirtyCheckStrategy;
    }

    protected AbstractTable(String tableName)
    {
        this(tableName,UpdateStrategy.DEFAULT,VerifyOnWriteStrategy.DEFAULT,DirtyCheckStrategy.DEFAULT);
    }

    @Override
    public String getTableName()
    {
        return tableName;
    }

    @Override
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    @Override
    public UpdateStrategy getUpdateStrategy()
    {
        return updateStrategy;
    }

    @Override
    public void setUpdateStrategy(UpdateStrategy updateStrategy)
    {
        this.updateStrategy = updateStrategy;
    }

    @Override
    public VerifyOnWriteStrategy getVerifyOnWriteStrategy()
    {
        return verifyOnWriteStrategy;
    }

    @Override
    public void setVerifyOnWriteStrategy(VerifyOnWriteStrategy verifyOnWriteStrategy)
    {
        this.verifyOnWriteStrategy = verifyOnWriteStrategy;
    }

    @Override
    public DirtyCheckStrategy getDirtyCheckStrategy()
    {
        return dirtyCheckStrategy;
    }

    @Override
    public void setDirtyCheckStrategy(DirtyCheckStrategy dirtyCheckStrategy)
    {
        this.dirtyCheckStrategy = dirtyCheckStrategy;
    }
}
