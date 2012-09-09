package dbgate;

import dbgate.caches.CacheManager;
import dbgate.caches.impl.EntityInfo;
import org.apache.derby.impl.io.VFMemoryStorageFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/31/12
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractDbGateTestBase
{
    protected static final String driverName = "org.apache.derby.jdbc.EmbeddedDriver";

    private static final HashMap<String,Collection<String>> dbTableNameMap = new HashMap<>();
    private static final HashMap<String,Collection<Class>> dbEntityTypeMap = new HashMap<>();

    protected static Class testClass = AbstractDbGateTestBase.class;
    protected static DefaultTransactionFactory connector;

    protected static void beginInit(String dbName)
    {
        try
        {
            Logger.getLogger(testClass.getName()).info("Starting in-memory database for unit tests");
            Class.forName(driverName);
            Connection con = DriverManager.getConnection(String.format("jdbc:derby:memory:%s;create=true",dbName));
            con.close();

            if (connector == null)
            {
                connector = new DefaultTransactionFactory(String.format("jdbc:derby:memory:%s;",dbName)
                        ,driverName,DefaultTransactionFactory.DB_DERBY);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(testClass.getName()).severe(String.format("Exception during database %s startup.",dbName));
        }
    }

    protected static void createTableFromSql(String sql,String dbName)
    {
        try
        {
            Connection con = DriverManager.getConnection(String.format("jdbc:derby:memory:%s",dbName));
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();
            con.close();

            addTableNameFromSql(sql,dbName);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(testClass.getName()).severe(String.format("Exception creating the table with sql %s in database %s.",sql,dbName));
        }
    }

    protected static void registerClassForDbPatching(Class entity,String dbName)
    {
        Collection<Class> entityTypes;
        if (dbEntityTypeMap.containsKey(dbName))
        {
            entityTypes = dbEntityTypeMap.get(dbName);
        }
        else
        {
            entityTypes = new ArrayList<>();
            dbEntityTypeMap.put(dbName,entityTypes);
        }
        entityTypes.add(entity);
    }

    private static void addTableNameFromSql(String sql,String dbName)
    {
        Pattern pattern = Pattern.compile("(create)([\\s]*)(table)([\\s]*)([^\\s]*)",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find())
        {
            String tableName = matcher.group(matcher.groupCount());
            addTableName(tableName,dbName);
        }
    }

    protected static void endInit(String dbName)
    {
        try
        {
            if (dbEntityTypeMap.containsKey(dbName))
            {
                Collection<Class> typeList = dbEntityTypeMap.get(dbName);
                if (typeList.size() > 0)
                {
                    ITransaction tx = connector.createTransaction();
                    connector.getDbGate().patchDataBase(tx,typeList,true);
                    tx.commit();
                    tx.close();
                }

                for (Class aClass : typeList)
                {
                    addTableNameFromEntity(aClass,dbName);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(testClass.getName()).severe(String.format("Exception patching the database %s.",dbName));
        }
    }

    private static void addTableNameFromEntity(Class entityType,String dbName)
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(entityType);
        while (entityInfo != null)
        {
            addTableName(entityInfo.getTableInfo().getTableName(),dbName);
            entityInfo = entityInfo.getSuperEntityInfo();
        }
    }

    private static void addTableName(String tableName,String dbName)
    {
        Collection<String> tableNames;
        if (dbTableNameMap.containsKey(dbName))
        {
            tableNames = dbTableNameMap.get(dbName);
        }
        else
        {
            tableNames = new ArrayList<>();
            dbTableNameMap.put(dbName, tableNames);
        }
        if (!tableNames.contains(tableName))
        {
            tableNames.add(tableName);
        }
    }

    protected static void cleanupDb(String dbName)
    {
        try
        {
            if (dbTableNameMap.containsKey(dbName))
            {
                Collection<String> tableNames = dbTableNameMap.get(dbName);
                if (tableNames.size() > 0)
                {
                    Connection con = DriverManager.getConnection(String.format("jdbc:derby:memory:%s", dbName));
                    for (String tableName : tableNames)
                    {
                        PreparedStatement ps = con.prepareStatement(String.format("DELETE FROM %s",tableName));
                        ps.execute();
                        ps.close();
                    }
                    con.close();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(testClass.getName()).severe(String.format("Exception cleaning the database %s.",dbName));
        }
    }

    protected static void finalizeDb(String dbName)
    {
        connector = null;
        Logger.getLogger(DbGateDirtyCheckTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection(String.format("jdbc:derby:memory:%s;shutdown=true",dbName)).close();
        }
        catch (SQLException ex)
        {
            if (ex.getErrorCode() != 45000)
            {
                ex.printStackTrace();
                Logger.getLogger(testClass.getName()).severe(String.format("Exception finalizing the database %s.",dbName));
            }
        }
        try
        {
            VFMemoryStorageFactory.purgeDatabase(new File(dbName).getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
            Logger.getLogger(testClass.getName()).severe(String.format("Exception finalizing the database %s.",dbName));
        }

        dbEntityTypeMap.clear();
        dbTableNameMap.clear();
    }
}
