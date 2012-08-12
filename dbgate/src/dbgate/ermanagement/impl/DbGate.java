package dbgate.ermanagement.impl;

import dbgate.*;
import dbgate.DBConnector;
import dbgate.caches.CacheManager;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.exceptions.common.DBConnectorNotInitializedException;
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
public class DbGate implements IDbGate
{
    private static IDbGate erLayer;

    private PersistRetrievalLayer persistRetrievalLayer;
    private DataMigrationLayer dataMigrationLayer;
    private IDbGateConfig config;
    private IDbGateStatistics statistics;

    private DbGate() throws DBConnectorNotInitializedException
    {
        if (DBConnector.getSharedInstance() == null)
        {
            throw new DBConnectorNotInitializedException("The DBConnector is not initialized");
        }
        int dbType = DBConnector.getSharedInstance().getDbType();
        this.config = new DbGateConfig();
        this.statistics = new DbGateStatistics();
        initializeDefaults();

        IDBLayer dbLayer = LayerFactory.createLayer(dbType);
        CacheManager.init(config);
        persistRetrievalLayer = new PersistRetrievalLayer(dbLayer,statistics,config);
        dataMigrationLayer = new DataMigrationLayer(dbLayer,statistics,config);
    }

    private void initializeDefaults()
    {
        config.setAutoTrackChanges(true);
        config.setLoggerName("ER-LAYER");
    }

    public void load(IReadOnlyEntity readOnlyEntity, ResultSet rs, Connection con) throws RetrievalException
    {
        persistRetrievalLayer.load(readOnlyEntity, rs, con);
    }

    public void save(IEntity entity, Connection con) throws PersistException
    {
        persistRetrievalLayer.save(entity, con);
    }

    @Override
    public Collection select(ISelectionQuery query,Connection con ) throws RetrievalException
    {
        return persistRetrievalLayer.select(query,con);
    }

    public void patchDataBase(Connection con, Collection<Class> entityTypes, boolean dropAll) throws DBPatchingException
    {
        dataMigrationLayer.patchDataBase(con, entityTypes, dropAll);
    }

    public void clearCache()
    {
        persistRetrievalLayer.clearCache();
    }

    @Override
    public void registerEntity(Class type, String tableName, Collection<IField> fields)
    {
        persistRetrievalLayer.registerEntity(type, tableName, fields);
    }

    public IDbGateConfig getConfig()
    {
        return config;
    }

    @Override
    public IDbGateStatistics getStatistics()
    {
        return statistics;
    }

    public static IDbGate getSharedInstance()
    {
        if (erLayer == null)
        {
            try
            {
                erLayer = new DbGate();
            }
            catch (DBConnectorNotInitializedException e)
            {
                e.printStackTrace();
            }
        }
        return erLayer;
    }
}
