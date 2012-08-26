package dbgate.ermanagement.ermapper;

import dbgate.*;
import dbgate.caches.CacheManager;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.LayerFactory;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;

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
    private PersistRetrievalLayer persistRetrievalLayer;
    private DataMigrationLayer dataMigrationLayer;
    private IDbGateConfig config;
    private IDbGateStatistics statistics;

    public DbGate(int dbType)
    {
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

    public void load(IReadOnlyEntity readOnlyEntity, ResultSet rs, ITransaction tx) throws RetrievalException
    {
        persistRetrievalLayer.load(readOnlyEntity, rs, tx);
    }

    public void save(IEntity entity, ITransaction tx) throws PersistException
    {
        persistRetrievalLayer.save(entity, tx);
    }

    @Override
    public Collection select(ISelectionQuery query,ITransaction tx ) throws RetrievalException
    {
        return persistRetrievalLayer.select(query,tx);
    }

    public void patchDataBase(ITransaction tx, Collection<Class> entityTypes, boolean dropAll) throws DBPatchingException
    {
        dataMigrationLayer.patchDataBase(tx, entityTypes, dropAll);
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
}
