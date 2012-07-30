package dbgate.ermanagement.impl;

import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.*;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecInfo;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryParam;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 25, 2010
 * Time: 8:27:07 PM
 */
public class ERDataManager implements IERDataManager
{
    private ERDataRetrievalManager erDataRetrievalManager;
    private ERDataPersistManager erDataPersistManager;
    private IDBLayer dbLayer;
    private IERLayerConfig config;

    public ERDataManager(IDBLayer dbLayer,IERLayerStatistics statistics,IERLayerConfig config)
    {
        this.erDataRetrievalManager = new ERDataRetrievalManager(dbLayer,statistics,config);
        this.erDataPersistManager = new ERDataPersistManager(dbLayer,statistics,config);
        this.config = config;
        this.dbLayer = dbLayer;
    }

    public void load(ServerRODBClass roEntity, ResultSet rs, Connection con) throws RetrievalException
    {
        erDataRetrievalManager.load(roEntity,rs,con);
    }

    public void save(ServerDBClass entity,Connection con ) throws PersistException
    {
        erDataPersistManager.save(entity,con);
    }

    @Override
    public Collection select(ISelectionQuery query,Connection con ) throws RetrievalException
    {
        ResultSet rs = null;
        try
        {
            StringBuilder logSb = new StringBuilder();
            boolean showQuery = config.isShowQueries();
            QueryExecInfo execInfo = dbLayer.getDataManipulate().createExecInfo(con, query);
            if (showQuery)
            {
                logSb.append(execInfo.getSql());
                for (QueryParam param : execInfo.getParams())
                {
                    logSb.append(" ,").append("Param").append(param.getIndex()).append("=").append(param.getValue());
                }
                Logger.getLogger(config.getLoggerName()).info(logSb.toString());
            }

            rs = dbLayer.getDataManipulate().createResultSet(con,execInfo);

            Collection retList = new ArrayList();
            Collection<IQuerySelection> selections = query.getStructure().getSelectList();

            while (rs.next())
            {
                int count = 0;
                Object[]  rowObjects = new Object[selections.size()];
                for (IQuerySelection selection : selections)
                {
                    Object loaded = selection.retrieve(rs);
                    rowObjects[count++] = loaded;
                }
                retList.add(rowObjects);
            }

            return retList;
        }
        catch (Exception e)
        {
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE,e.getMessage(),e);
            throw new RetrievalException(e.getMessage(),e);
        }
        finally
        {
            DBMgmtUtility.close(rs);
        }
    }

    public void clearCache()
    {
        CacheManager.fieldCache.clear();
        CacheManager.methodCache.clear();
        CacheManager.tableCache.clear();
        CacheManager.queryCache.clear();
    }

    public void registerTable(Class type, String tableName)
    {
        CacheManager.tableCache.register(type,tableName);
    }

    public void registerFields(Class type, Collection<IField> fields)
    {
        CacheManager.fieldCache.register(type,fields);
    }
}
