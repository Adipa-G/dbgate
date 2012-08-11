package dbgate.ermanagement.impl;

import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.ermanagement.*;
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
public class ERDataManager implements IERDataManager
{
    private ERDataRetrievalManager erDataRetrievalManager;
    private ERDataPersistManager erDataPersistManager;

    public ERDataManager(IDBLayer dbLayer,IERLayerStatistics statistics,IERLayerConfig config)
    {
        this.erDataRetrievalManager = new ERDataRetrievalManager(dbLayer,statistics,config);
        this.erDataPersistManager = new ERDataPersistManager(dbLayer,statistics,config);
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
        return erDataRetrievalManager.select(query,con);
    }

    public void clearCache()
    {
        CacheManager.clear();
    }

    @Override
    public void registerEntity(Class type, String tableName, Collection<IField> fields)
    {
        CacheManager.register(type, tableName, fields);
    }
}
