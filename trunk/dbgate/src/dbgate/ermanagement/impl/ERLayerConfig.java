package dbgate.ermanagement.impl;

import dbgate.ermanagement.IERLayerConfig;

/**
 * Date: Mar 22, 2011
 * Time: 10:27:49 PM
 */
public class ERLayerConfig implements IERLayerConfig
{
    private boolean autoTrackChanges;
    private boolean showQueries;
    private boolean checkVersion;
    private boolean enableStatistics;
    private String loggerName;

    public ERLayerConfig()
    {
        autoTrackChanges = true;
        showQueries = true;
        checkVersion = true;
        enableStatistics = false;
    }

    @Override
    public boolean isAutoTrackChanges()
    {
        return autoTrackChanges;
    }

    @Override
    public void setAutoTrackChanges(boolean autoTrackChanges)
    {
        this.autoTrackChanges = autoTrackChanges;
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
    public boolean isCheckVersion()
    {
        return checkVersion;
    }

    @Override
    public void setCheckVersion(boolean checkVersion)
    {
        this.checkVersion = checkVersion;
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
}
