package dbgate;

import dbgate.ermanagement.impl.DbGate;
import dbgate.support.patch.patchtabledifferences.FourColumnEntity;
import dbgate.support.patch.patchtabledifferences.ThreeColumnEntity;
import dbgate.support.patch.patchtabledifferences.ThreeColumnTypeDifferentEntity;
import junit.framework.Assert;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Aug 29, 2010
 * Time: 6:40:58 PM
 */
public class DbGatePatchTableDifferenceDBTests
{
    private static DBConnector connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DbGatePatchTableDifferenceDBTests.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-metadata-table-difference;create=true");

            connector = new DBConnector("jdbc:derby:memory:unit-testing-metadata-table-difference;","org.apache.derby.jdbc.EmbeddedDriver",
                                        DBConnector.DB_DERBY);

            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(false);
            DbGate.getSharedInstance().getConfig().setCheckVersion(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DbGatePatchTableDifferenceDBTests.class.getName()).severe("Exception during database startup.");
        }
    }

    @Test
    public void patchDifference_patchDB_withTableColumnAdded_shouldAddColumn()
    {
        try
        {
            Connection connection = connector.getConnection();
            Collection<Class> dbClasses = new ArrayList<>();
            dbClasses.add(ThreeColumnEntity.class);
            DbGate.getSharedInstance().patchDataBase(connection,dbClasses,true);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            dbClasses = new ArrayList<>();
            dbClasses.add(FourColumnEntity.class);
            DbGate.getSharedInstance().patchDataBase(connection,dbClasses,false);
            connection.commit();
            connection.close();

            int id = 35;

            connection = connector.getConnection();
            FourColumnEntity columnEntity = createFourColumnEntity(id);
            columnEntity.persist(connection);
            loadFourColumnEntityWithId(connection,id);
            connection.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void patchDifference_patchDB_withTableColumnDeleted_shouldDeleteColumn()
    {
        try
        {
            Connection connection = connector.getConnection();
            Collection<Class> dbClasses = new ArrayList<>();
            dbClasses.add(FourColumnEntity.class);
            DbGate.getSharedInstance().patchDataBase(connection,dbClasses,true);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            dbClasses = new ArrayList<>();
            dbClasses.add(ThreeColumnEntity.class);
            DbGate.getSharedInstance().patchDataBase(connection,dbClasses,false);
            connection.commit();
            connection.close();

            int id = 35;

            connection = connector.getConnection();
            FourColumnEntity columnEntity = createFourColumnEntity(id);
            try
            {
                columnEntity.persist(connection);
                Assert.fail("object should not be able to persist");
            }
            catch (Exception ex)
            {
                Assert.assertTrue(true);
            }
            connection.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void patchDifference_patchDB_withTableColumnChanged_shouldUpdateColumn()
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 220; i++)
            {
                sb.append("a");
            }

            Connection connection = connector.getConnection();
            Collection<Class> dbClasses = new ArrayList<>();
            dbClasses.add(ThreeColumnEntity.class);
            DbGate.getSharedInstance().patchDataBase(connection,dbClasses,true);
            connection.commit();
            connection.close();

            int id = 34;
            connection = connector.getConnection();
            ThreeColumnEntity columnEntity = createThreeColumnEntity(id);
            columnEntity.setName(sb.toString());
            try
            {
                columnEntity.persist(connection);
                Assert.fail("Object should not be able to persist");
            }
            catch (Exception ex)
            {
                //eat it
            }
            connection.close();


            connection = connector.getConnection();
            dbClasses = new ArrayList<>();
            dbClasses.add(ThreeColumnTypeDifferentEntity.class);
            DbGate.getSharedInstance().patchDataBase(connection,dbClasses,false);
            connection.commit();
            connection.close();

            id = 35;
            connection = connector.getConnection();
            columnEntity = createThreeColumnEntity(id);
            columnEntity.setName(sb.toString());
            columnEntity.persist(connection);
            connection.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private FourColumnEntity loadFourColumnEntityWithId(Connection connection,int id) throws Exception
    {
        FourColumnEntity loadedEntity = null;

        PreparedStatement ps = connection.prepareStatement("select * from table_change_test_entity where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadedEntity = new FourColumnEntity();
            loadedEntity.retrieve(rs,connection);
        }
        rs.close();
        ps.close();
        connection.close();

        return loadedEntity;
    }
    
    private FourColumnEntity createFourColumnEntity(int id)
    {
        FourColumnEntity entity = new FourColumnEntity();
        entity.setIdCol(id);
        entity.setCode("4C");
        entity.setName("4Col");
        entity.setIndexNo(0);
        return entity;
    }
    
    private ThreeColumnEntity createThreeColumnEntity(int id)
    {
        ThreeColumnEntity entity = new ThreeColumnEntity();
        entity.setIdCol(id);
        entity.setName("3Col");
        entity.setIndexNo(0);
        return entity;
    }

    @AfterClass
    public static void after()
    {
        Logger.getLogger(DbGatePatchTableDifferenceDBTests.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-metadata-table-difference;shutdown=true").close();
        }
        catch (SQLException ex)
        {
            if (ex.getErrorCode() != 45000)
            {
                ex.printStackTrace();
            }
        }
        try
        {
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-metadata-table-difference").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}