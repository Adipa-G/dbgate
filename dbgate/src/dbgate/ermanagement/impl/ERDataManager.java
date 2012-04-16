package dbgate.ermanagement.impl;

import dbgate.DBClassStatus;
import dbgate.DBColumnType;
import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.context.*;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.context.impl.ERSession;
import dbgate.ermanagement.context.impl.EntityRelationFieldValueList;
import dbgate.ermanagement.context.impl.EntityTypeFieldValueList;
import dbgate.ermanagement.exceptions.*;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.impl.utils.ERSessionUtils;
import dbgate.ermanagement.impl.utils.MiscUtils;
import dbgate.ermanagement.impl.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
