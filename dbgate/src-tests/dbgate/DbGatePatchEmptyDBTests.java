package dbgate;

import dbgate.exceptions.PersistException;
import dbgate.support.patch.patchempty.LeafEntity;
import dbgate.support.patch.patchempty.LeafEntitySubA;
import dbgate.support.patch.patchempty.LeafEntitySubB;
import dbgate.support.patch.patchempty.RootEntity;
import junit.framework.Assert;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class DbGatePatchEmptyDBTests
{
    private static DefaultTransactionFactory connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DbGatePatchEmptyDBTests.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-metadata-empty;create=true");

            connector = new DefaultTransactionFactory("jdbc:derby:memory:unit-testing-metadata-empty;","org.apache.derby.jdbc.EmbeddedDriver",
                                        DefaultTransactionFactory.DB_DERBY);

            connector.getDbGate().getConfig().setAutoTrackChanges(false);
            connector.getDbGate().getConfig().setCheckVersion(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DbGatePatchEmptyDBTests.class.getName()).severe("Exception during database startup.");
        }
    }

    @Test
    public void patchEmpty_patchDataBase_withEmptyDb_shouldCreateTables_shouldBeAbleToInsertData()
    {
        try
        {
            ITransaction tx = connector.createTransaction();

            Collection<Class> dbClasses = new ArrayList<>();
            dbClasses.add(LeafEntitySubA.class);
            dbClasses.add(LeafEntitySubB.class);
            dbClasses.add(RootEntity.class);
            connector.getDbGate().patchDataBase(tx,dbClasses,true);

            int id = 35;
            RootEntity entity = createRootEntityWithoutNullValues(id);
            entity.getLeafEntities().add(createLeafEntityA(id,1));
            entity.getLeafEntities().add(createLeafEntityB(id,2));
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            RootEntity loadedEntity = loadRootEntityWithId(tx,id);
            tx.close();

            assertTwoRootEntitiesEquals(entity,loadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }


    @Test(expected = PersistException.class)
    public void patchEmpty_patchDataBase_withEmptyDb_shouldCreatePrimaryKeys_shouldNotAbleToPutDuplicateData() throws Exception
    {
        ITransaction tx = connector.createTransaction();

        Collection<Class> dbClasses = new ArrayList<>();
        dbClasses.add(LeafEntitySubA.class);
        dbClasses.add(LeafEntitySubB.class);
        dbClasses.add(RootEntity.class);
        connector.getDbGate().patchDataBase(tx,dbClasses,true);

        int id = 35;
        RootEntity entity = createRootEntityWithoutNullValues(id);
        entity.getLeafEntities().add(createLeafEntityA(id,1));
        entity.getLeafEntities().add(createLeafEntityB(id,1));
        entity.persist(tx);
        tx.commit();
        tx.close();

        tx = connector.createTransaction();
        RootEntity loadedEntity = loadRootEntityWithId(tx,id);
        tx.close();

        assertTwoRootEntitiesEquals(entity,loadedEntity);
    }

    @Test(expected = PersistException.class)
    public void patchEmpty_patchDataBase_withEmptyDb_shouldCreateForeignKeys_shouldNotAbleToInconsistantData() throws Exception
    {
        ITransaction tx = connector.createTransaction();

        Collection<Class> dbClasses = new ArrayList<>();
        dbClasses.add(LeafEntitySubA.class);
        dbClasses.add(LeafEntitySubB.class);
        dbClasses.add(RootEntity.class);
        connector.getDbGate().patchDataBase(tx,dbClasses,true);

        int id = 35;
        RootEntity entity = createRootEntityWithoutNullValues(id);
        entity.getLeafEntities().add(createLeafEntityA(id + 1,1));
        entity.getLeafEntities().add(createLeafEntityB(id,1));
        entity.persist(tx);
        tx.commit();
        tx.close();

        tx = connector.createTransaction();
        RootEntity loadedEntity = loadRootEntityWithId(tx,id);
        tx.close();

        assertTwoRootEntitiesEquals(entity,loadedEntity);
    }

    @Test
    public void patchEmpty_patchDataBase_pathTwice_shouldNotThrowException() throws Exception
    {
        ITransaction tx = connector.createTransaction();

        Collection<Class> dbClasses = new ArrayList<>();
        dbClasses.add(LeafEntitySubA.class);
        dbClasses.add(LeafEntitySubB.class);
        dbClasses.add(RootEntity.class);

        connector.getDbGate().patchDataBase(tx,dbClasses,true);
        
        connector.getDbGate().patchDataBase(tx,dbClasses,true);
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

    private RootEntity loadRootEntityWithId(ITransaction tx,int id) throws Exception
    {
        RootEntity loadedEntity = null;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from root_entity where id_col = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadedEntity = new RootEntity();
            loadedEntity.retrieve(rs,tx);
        }
        rs.close();
        ps.close();
        tx.close();

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
        Logger.getLogger(DbGatePatchEmptyDBTests.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-metadata-empty;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-metadata-empty").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}