package dbgate;

/**
 * Date: Mar 22, 2011
 * Time: 10:32:05 PM
 */
public interface IDbGateConfig
{
    boolean isShowQueries();

    void setShowQueries(boolean showQueries);

    boolean isEnableStatistics();

    void setEnableStatistics(boolean enableStatistics);

    String getLoggerName();

    void setLoggerName(String loggerName);

    DirtyCheckStrategy getDefaultDirtyCheckStrategy();

    void setDefaultDirtyCheckStrategy(DirtyCheckStrategy strategy);

    VerifyOnWriteStrategy getDefaultVerifyOnWriteStrategy();

    void setDefaultVerifyOnWriteStrategy(VerifyOnWriteStrategy strategy);

    UpdateStrategy getDefaultUpdateStrategy();

    void setDefaultUpdateStrategy(UpdateStrategy strategy);

    FetchStrategy getDefaultFetchStrategy();

    void setDefaultFetchStrategy(FetchStrategy strategy);
}
