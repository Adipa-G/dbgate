package dbgate.ermanagement;

/**
 * Date: Mar 22, 2011
 * Time: 10:32:05 PM
 */
public interface IDbGateConfig
{
    boolean isAutoTrackChanges();

    void setAutoTrackChanges(boolean autoTrackChanges);

    String getLoggerName();

    void setLoggerName(String loggerName);

    boolean isShowQueries();

    void setShowQueries(boolean showQueries);

    boolean isCheckVersion();

    void setCheckVersion(boolean checkVersion);

    boolean isEnableStatistics();

    void setEnableStatistics(boolean enableStatistics);

    boolean isUpdateChangedColumnsOnly();

    void setUpdateChangedColumnsOnly(boolean updateChangedColumnsOnly);
}
