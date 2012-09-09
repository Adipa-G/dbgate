package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/9/12
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITable
{
    String getTableName();

    void setTableName(String tableName);

    UpdateStrategy getUpdateStrategy();

    void setUpdateStrategy(UpdateStrategy updateStrategy);

    VerifyOnWriteStrategy getVerifyOnWriteStrategy();

    void setVerifyOnWriteStrategy(VerifyOnWriteStrategy verifyOnWriteStrategy);

    DirtyCheckStrategy getDirtyCheckStrategy();

    void setDirtyCheckStrategy(DirtyCheckStrategy dirtyCheckStrategy);
}
