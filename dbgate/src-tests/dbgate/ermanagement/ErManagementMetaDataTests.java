package dbgate.ermanagement;

import dbgate.DateWrapper;
import dbgate.ServerDBClass;
import dbgate.TimeStampWrapper;
import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.exceptions.MetaDataException;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.ermanagement.support.metadata.LeafEntity;
import dbgate.ermanagement.support.metadata.LeafEntitySubA;
import dbgate.ermanagement.support.metadata.LeafEntitySubB;
import dbgate.ermanagement.support.metadata.RootEntity;
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
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Aug 29, 2010
 * Time: 6:40:58 PM
 */
public class ErManagementMetaDataTests
{
    private static DBConnector connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(ErManagementMetaDataTests.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-metadata;create=true");

            connector = new DBConnector("jdbc:derby:memory:unit-testing-metadata;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);

            ERLayer.getSharedInstance().getConfig().setAutoTrackChanges(false);
            ERLayer.getSharedInstance().getConfig().setCheckVersion(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(ErManagementMetaDataTests.class.getName()).severe("Exception during database startup.");
        }
    }

    @Test
    public void ERLayer_patchDataBase_withEmptyDb_shouldCreateTables_shouldBeAbleToInsertData()
    {
        try
        {
            Connection connection = connector.getConnection();

            Collection<ServerDBClass> dbClasses = new ArrayList<ServerDBClass>();
            dbClasses.add(new LeafEntitySubA());
            dbClasses.add(new LeafEntitySubB());
            dbClasses.add(new RootEntity());
            ERLayer.getSharedInstance().patchDataBase(connection,dbClasses,true);

            int id = 35;
            RootEntity entity = createRootEntityWithoutNullValues(id);
            entity.getLeafEntities().add(createLeafEntityA(id,1));
            entity.getLeafEntities().add(createLeafEntityB(id,2));
            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            RootEntity loadedEntity = loadRootEntityWithId(connection,id);
            connection.close();

            assertTwoRootEntitiesEquals(entity,loadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }


    @Test(expected = PersistException.class)
    public void ERLayer_patchDataBase_withEmptyDb_shouldCreatePrimaryKeys_shouldNotAbleToPutDuplicateData() throws Exception
    {
        Connection connection = connector.getConnection();

        Collection<ServerDBClass> dbClasses = new ArrayList<ServerDBClass>();
        dbClasses.add(new LeafEntitySubA());
        dbClasses.add(new LeafEntitySubB());
        dbClasses.add(new RootEntity());
        ERLayer.getSharedInstance().patchDataBase(connection,dbClasses,true);

        int id = 35;
        RootEntity entity = createRootEntityWithoutNullValues(id);
        entity.getLeafEntities().add(createLeafEntityA(id,1));
        entity.getLeafEntities().add(createLeafEntityB(id,1));
        entity.persist(connection);
        connection.commit();
        connection.close();

        connection = connector.getConnection();
        RootEntity loadedEntity = loadRootEntityWithId(connection,id);
        connection.close();

        assertTwoRootEntitiesEquals(entity,loadedEntity);
    }

    @Test(expected = PersistException.class)
    public void ERLayer_patchDataBase_withEmptyDb_shouldCreateForeignKeys_shouldNotAbleToInconsistantData() throws Exception
    {
        Connection connection = connector.getConnection();

        Collection<ServerDBClass> dbClasses = new ArrayList<ServerDBClass>();
        dbClasses.add(new LeafEntitySubA());
        dbClasses.add(new LeafEntitySubB());
        dbClasses.add(new RootEntity());
        ERLayer.getSharedInstance().patchDataBase(connection,dbClasses,true);

        int id = 35;
        RootEntity entity = createRootEntityWithoutNullValues(id);
        entity.getLeafEntities().add(createLeafEntityA(id + 1,1));
        entity.getLeafEntities().add(createLeafEntityB(id,1));
        entity.persist(connection);
        connection.commit();
        connection.close();

        connection = connector.getConnection();
        RootEntity loadedEntity = loadRootEntityWithId(connection,id);
        connection.close();

        assertTwoRootEntitiesEquals(entity,loadedEntity);
    }

    @Test
    public void ERLayer_patchDataBase_pathTwice_shouldNotThrowException() throws Exception
    {
        Connection connection = connector.getConnection();

        Collection<ServerDBClass> dbClasses = new ArrayList<ServerDBClass>();
        dbClasses.add(new LeafEntitySubA());
        dbClasses.add(new LeafEntitySubB());
        dbClasses.add(new RootEntity());

        ERLayer.getSharedInstance().patchDataBase(connection,dbClasses,true);
        
        ERLayer.getSharedInstance().patchDataBase(connection,dbClasses,true);
    }

    private void assertTwoRootEntitiesEquals(RootEntity entityA, RootEntity entityB)
    {
        Assert.assertEquals(entityA.getIdCol(),entityB.getIdCol());

        Assert.assertEquals(entityA.getCharNotNull(),entityB.getCharNotNull());
        Assert.assertEquals(entityA.getCharNull(),entityB.getCharNull());
        Assert.assertEquals(entityA.getDateNotNull(),entityB.getDateNotNull());
        Assert.assertEquals(entityA.getDateNull(),entityB.getDateNull());
        Assert.assertEquals(entityA.getDoubleNotNull(),entityB.getDoubleNotNull());
        Assert.assertEquals(entityA.getDoubleNull(),entityB.getDoubleNull());
        Assert.assertEquals(entityA.getFloatNotNull(),entityB.getFloatNotNull());
        Assert.assertEquals(entityA.getFloatNull(),entityB.getFloatNull());
        Assert.assertEquals(entityA.getIntNotNull(),entityB.getIntNotNull());
        Assert.assertEquals(entityA.getIntNull(),entityB.getIntNull());
        Assert.assertEquals(entityA.getLongNotNull(),entityB.getLongNotNull());
        Assert.assertEquals(entityA.getLongNull(),entityB.getLongNull());
        Assert.assertEquals(entityA.getTimestampNotNull(),entityB.getTimestampNotNull());
        Assert.assertEquals(entityA.getTimestampNull(),entityB.getTimestampNull());
        Assert.assertEquals(entityA.getVarcharNotNull(),entityB.getVarcharNotNull());
        Assert.assertEquals(entityA.getVarcharNull(),entityB.getVarcharNull());
        Assert.assertEquals(entityA.getLeafEntities().size(),entityB.getLeafEntities().size());

        Iterator<LeafEntity> iteratorA = entityA.getLeafEntities().iterator();
        Iterator<LeafEntity> iteratorB = entityB.getLeafEntities().iterator();
        while (iteratorA.hasNext())
        {
            LeafEntity leafEntityA = iteratorA.next();
            LeafEntity leafEntityB = iteratorB.next();
            assertTwoLeafEntitiesTypeAEquals(leafEntityA,leafEntityB);
        }
    }

    private void assertTwoLeafEntitiesTypeAEquals(LeafEntity entityA, LeafEntity entityB)
    {
        Assert.assertEquals(entityA.getIdCol(),entityB.getIdCol());
        Assert.assertEquals(entityA.getIndexNo(),entityB.getIndexNo());
        Assert.assertEquals(entityA.getSomeText(),entityB.getSomeText());
        if (entityA instanceof LeafEntitySubA && entityB instanceof LeafEntitySubA)
        {
            Assert.assertEquals(((LeafEntitySubA)entityA).getSomeTextA(),((LeafEntitySubA)entityB).getSomeTextA());
        }
        if (entityA instanceof LeafEntitySubB && entityB instanceof LeafEntitySubB)
        {
            Assert.assertEquals(((LeafEntitySubB)entityA).getSomeTextB(),((LeafEntitySubB)entityB).getSomeTextB());
        }
    }

    private RootEntity loadRootEntityWithId(Connection connection,int id) throws Exception
    {
        RootEntity loadedEntity = null;

        PreparedStatement ps = connection.prepareStatement("select * from root_entity where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadedEntity = new RootEntity();
            loadedEntity.retrieve(rs,connection);
        }
        rs.close();
        ps.close();
        connection.close();

        return loadedEntity;
    }

    private LeafEntity createLeafEntityA(int id,int index)
    {
        LeafEntitySubA leafEntity = new LeafEntitySubA();
        leafEntity.setIdCol(id);
        leafEntity.setIndexNo(index);
        leafEntity.setSomeTextA("text A");
        leafEntity.setSomeText("Id : " + id + " - " + " Index : " + index);

        return leafEntity;
    }

    private LeafEntity createLeafEntityB(int id,int index)
    {
        LeafEntitySubB leafEntity = new LeafEntitySubB();
        leafEntity.setIdCol(id);
        leafEntity.setIndexNo(index);
        leafEntity.setSomeTextB("text B");
        leafEntity.setSomeText("Id : " + id + " - " + " Index : " + index);

        return leafEntity;
    }

    private RootEntity createRootEntityWithoutNullValues(int id)
    {
        RootEntity entity = new RootEntity();
        entity.setIdCol(id);

        entity.setBooleanNotNull(true);
        entity.setBooleanNull(true);
        entity.setCharNotNull('A');
        entity.setCharNull('B');
        entity.setDateNotNull(new DateWrapper());
        entity.setDateNull(new DateWrapper());
        entity.setDoubleNotNull(5D);
        entity.setDoubleNull(6D);
        entity.setFloatNotNull(20F);
        entity.setFloatNull(20F);
        entity.setIntNotNull(24);
        entity.setIntNull(23);
        entity.setLongNotNull(356L);
        entity.setLongNull(326L);
        entity.setTimestampNotNull(new TimeStampWrapper());
        entity.setTimestampNull(new TimeStampWrapper());
        entity.setVarcharNotNull("notNull");
        entity.setVarcharNull("null");

        return entity;
    }

    @AfterClass
    public static void after()
    {
        Logger.getLogger(ErManagementMetaDataTests.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-metadata;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-metadata").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}