package dbgate.ermanagement.ermapper;

import dbgate.*;
import dbgate.caches.CacheManager;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;

import java.sql.ResultSet;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 25, 2010
 * Time: 8:27:07 PM
 */
public class PersistRetrievalLayer
{
    private RetrievalOperationLayer retrievalOperationLayer;
    private PersistOperationLayer persistOperationLayer;

    public PersistRetrievalLayer(IDBLayer dbLayer, IDbGateStatistics statistics, IDbGateConfig config)
    {
        this.retrievalOperationLayer = new RetrievalOperationLayer(dbLayer,statistics,config);
        this.persistOperationLayer = new PersistOperationLayer(dbLayer,statistics,config);
    }

    public void load(IReadOnlyEntity roEntity, ResultSet rs, ITransaction tx) throws RetrievalException
    {
        retrievalOperationLayer.load(roEntity,rs,tx);
    }

    public void save(IEntity entity,ITransaction tx ) throws PersistException
    {
        persistOperationLayer.save(entity,tx);
    }

    public Collection select(ISelectionQuery query,ITransaction tx ) throws RetrievalException
    {
        return retrievalOperationLayer.select(query,tx);
    }

    public void clearCache()
    {
        CacheManager.clear();
    }

    public void registerEntity(Class type, String tableName, Collection<IField> fields)
    {
        CacheManager.register(type, tableName, fields);
    }
}
