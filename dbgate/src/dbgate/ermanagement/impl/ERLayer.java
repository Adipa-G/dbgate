package dbgate.ermanagement.impl;

import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.DBPatchingException;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.exceptions.common.DBConnectorNotInitializedException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.LayerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jul 5, 2008
 * Time: 3:34:56 PM
 */
public class ERLayer implements IERLayer
{
    private static IERLayer erLayer;

    private IERDataManager erDataManager;
    private ERMetaDataManager erMetaDataManager;
    private IERLayerConfig config;
    private IERLayerStatistics statistics;

    private ERLayer() throws DBConnectorNotInitializedException
    {
        if (DBConnector.getSharedInstance() == null)
        {
            throw new DBConnectorNotInitializedException("The DBConnector is not initialized");
        }
        int dbType = DBConnector.getSharedInstance().getDbType();
        this.config = new ERLayerConfig();
        this.statistics = new ERLayerStatistics();
        initializeDefaults();

        IDBLayer dbLayer = LayerFactory.createLayer(dbType);
        CacheManager.init(config);
        erDataManager = new ERDataManager(dbLayer,statistics,config);
        erMetaDataManager = new ERMetaDataManager(dbLayer,statistics,config);
    }

    private void initializeDefaults()
    {
        config.setAutoTrackChanges(true);
        config.setLoggerName("ER-LAYER");
    }

    public void load(ServerRODBClass serverRODBClass, ResultSet rs, Connection con) throws RetrievalException
    {
        erDataManager.load(serverRODBClass, rs, con);
    }

    public void save(ServerDBClass serverDBClass, Connection con) throws PersistException
    {
        erDataManager.save(serverDBClass, con);
    }

    @Override
    public Collection select(ISelectionQuery query,Connection con ) throws RetrievalException
    {
        return erDataManager.select(query,con);
    }

    public void patchDataBase(Connection con, Collection<Class> entityTypes, boolean dropAll) throws DBPatchingException
    {
        erMetaDataManager.patchDataBase(con, entityTypes, dropAll);
    }

    public void clearCache()
    {
        erDataManager.clearCache();
    }

    @Override
    public void registerEntity(Class type, String tableName, Collection<IField> fields)
    {
        erDataManager.registerEntity(type, tableName, fields);
    }

    public IERLayerConfig getConfig()
    {
        return config;
    }

    @Override
    public IERLayerStatistics getStatistics()
    {
        return statistics;
    }

    public static IERLayer getSharedInstance()
    {
        if (erLayer == null)
        {
            try
            {
                erLayer = new ERLayer();
            }
            catch (DBConnectorNotInitializedException e)
            {
                e.printStackTrace();
            }
        }
        return erLayer;
    }
}
