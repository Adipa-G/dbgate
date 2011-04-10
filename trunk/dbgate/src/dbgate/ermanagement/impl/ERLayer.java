package dbgate.ermanagement.impl;

import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.IERDataManager;
import dbgate.ermanagement.IERLayer;
import dbgate.ermanagement.IERLayerConfig;
import dbgate.ermanagement.IField;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.DBConnectorNotInitializedException;
import dbgate.ermanagement.exceptions.DBPatchingException;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
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

    private ERLayer() throws DBConnectorNotInitializedException
    {
        if (DBConnector.getSharedInstance() == null)
        {
            throw new DBConnectorNotInitializedException("The DBConnector is not initialized");
        }
        int dbType = DBConnector.getSharedInstance().getDbType();
        this.config = new ERLayerConfig();
        initializeDefaults();

        IDBLayer dbLayer = LayerFactory.createLayer(dbType);
        CacheManager.init(dbLayer);
        erDataManager = new ERDataManager(dbLayer,config);
        erMetaDataManager = new ERMetaDataManager(dbLayer,config);
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

    public void patchDataBase(Connection con, Collection<ServerDBClass> dbClasses, boolean dropAll) throws DBPatchingException
    {
        erMetaDataManager.patchDataBase(con, dbClasses, dropAll);
    }

    public void clearCache()
    {
        erDataManager.clearCache();
    }

    public void registerTable(Class type, String tableName)
    {
        erDataManager.registerTable(type,tableName);
    }

    public void registerFields(Class type, Collection<IField> fields)
    {
        erDataManager.registerFields(type,fields);
    }

    public IERLayerConfig getConfig()
    {
        return config;
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
