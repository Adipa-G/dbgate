package dbgate.ermanagement.ermapper;

import dbgate.*;

/**
 * Date: Mar 22, 2011
 * Time: 10:27:49 PM
 */
public class DbGateConfig implements IDbGateConfig
{
    private String loggerName;
    private boolean showQueries;
    private boolean enableStatistics;

    private DirtyCheckStrategy dirtyCheckStrategy;
    private VerifyOnWriteStrategy verifyOnWriteStrategy;
    private UpdateStrategy updateStrategy;
    private FetchStrategy fetchStrategy;

    public DbGateConfig()
    {
        enableStatistics = false;
        showQueries = true;

        dirtyCheckStrategy = DirtyCheckStrategy.AUTOMATIC;
        verifyOnWriteStrategy = VerifyOnWriteStrategy.VERIFY;
        updateStrategy = UpdateStrategy.CHANGED_COLUMNS;
        fetchStrategy = FetchStrategy.EAGER;
    }

    @Override
    public String getLoggerName()
    {
        return loggerName;
    }

    @Override
    public void setLoggerName(String loggerName)
    {
        this.loggerName = loggerName;
    }

    @Override
    public boolean isEnableStatistics()
    {
        return enableStatistics;
    }

    @Override
    public void setEnableStatistics(boolean enableStatistics)
    {
        this.enableStatistics = enableStatistics;
    }

    @Override
    public boolean isShowQueries()
    {
        return showQueries;
    }

    @Override
    public void setShowQueries(boolean showQueries)
    {
        this.showQueries = showQueries;
    }

    @Override
    public DirtyCheckStrategy getDefaultDirtyCheckStrategy()
    {
        return dirtyCheckStrategy;
    }

    @Override
    public void setDefaultDirtyCheckStrategy(DirtyCheckStrategy strategy)
    {
        if (strategy != DirtyCheckStrategy.DEFAULT){
            dirtyCheckStrategy = strategy;
        }
    }

    @Override
    public VerifyOnWriteStrategy getDefaultVerifyOnWriteStrategy()
    {
        return verifyOnWriteStrategy;
    }

    @Override
    public void setDefaultVerifyOnWriteStrategy(VerifyOnWriteStrategy strategy)
    {
        if (strategy != VerifyOnWriteStrategy.DEFAULT){
            verifyOnWriteStrategy = strategy;
        }
    }

    @Override
    public UpdateStrategy getDefaultUpdateStrategy()
    {
        return updateStrategy;
    }

    @Override
    public void setDefaultUpdateStrategy(UpdateStrategy strategy)
    {
        if (strategy != UpdateStrategy.DEFAULT){
            updateStrategy = strategy;
        }
    }

    @Override
    public FetchStrategy getDefaultFetchStrategy()
    {
        return fetchStrategy;
    }

    @Override
    public void setDefaultFetchStrategy(FetchStrategy strategy)
    {
        if (strategy != FetchStrategy.DEFAULT){
            this.fetchStrategy = strategy;
        }
    }
}
