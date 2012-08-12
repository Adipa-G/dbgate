package dbgate.ermanagement.impl;

import dbgate.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;

import java.sql.Connection;
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

    public void load(IReadOnlyEntity roEntity, ResultSet rs, Connection con) throws RetrievalException
    {
        retrievalOperationLayer.load(roEntity,rs,con);
    }

    public void save(IEntity entity,Connection con ) throws PersistException
    {
        persistOperationLayer.save(entity,con);
    }

    public Collection select(ISelectionQuery query,Connection con ) throws RetrievalException
    {
        return retrievalOperationLayer.select(query,con);
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
