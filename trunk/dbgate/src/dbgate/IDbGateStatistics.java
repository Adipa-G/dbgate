package dbgate;

/**
 * Date: Mar 22, 2011
 * Time: 10:32:05 PM
 */
public interface IDbGateStatistics
{
    void reset();

    int getSelectQueryCount();

    int getInsertQueryCount();

    int getUpdateQueryCount();
    
    int getDeleteQueryCount();

    int getDbPatchQueryCount();
    
    int getSelectCount(Class type);

    int getInsertCount(Class type);

    int getUpdateCount(Class type);

    int getDeleteCount(Class type);
    
    void registerSelect(Class type);

    void registerInsert(Class type);

    void registerUpdate(Class type);

    void registerDelete(Class type);

    void registerPatch();
}
