package dbgate;

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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Aug 29, 2010
 * Time: 6:40:58 PM
 */
public class DbGatePatchTableDifferenceDBTests extends AbstractDbGateTestBase
{
    private static final String dbName = "unit-testing-metadata-table-difference";

    @BeforeClass
    public static void before()
    {
        testClass = DbGatePatchTableDifferenceDBTests.class;

        beginInit(dbName);
        endInit(dbName);

        connector.getDbGate().getConfig().setAutoTrackChanges(false);
        connector.getDbGate().getConfig().setCheckVersion(false);
    }

    @Test
    public void patchDifference_patchDB_withTableColumnAdded_shouldAddColumn()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            Collection<Class> dbClasses = new ArrayList<>();
            dbClasses.add(ThreeColumnEntity.class);
            connector.getDbGate().patchDataBase(tx,dbClasses,true);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            dbClasses = new ArrayList<>();
            dbClasses.add(FourColumnEntity.class);
            connector.getDbGate().patchDataBase(tx, dbClasses, false);
            tx.commit();
            tx.close();

            int id = 35;

            tx = connector.createTransaction();
            FourColumnEntity columnEntity = createFourColumnEntity(id);
            columnEntity.persist(tx);
            loadFourColumnEntityWithId(tx, id);
            tx.close();
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
            ITransaction tx = connector.createTransaction();
            Collection<Class> dbClasses = new ArrayList<>();
            dbClasses.add(FourColumnEntity.class);
            connector.getDbGate().patchDataBase(tx,dbClasses,true);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            dbClasses = new ArrayList<>();
            dbClasses.add(ThreeColumnEntity.class);
            connector.getDbGate().patchDataBase(tx, dbClasses, false);
            tx.commit();
            tx.close();

            int id = 35;

            tx = connector.createTransaction();
            FourColumnEntity columnEntity = createFourColumnEntity(id);
            try
            {
                columnEntity.persist(tx);
                Assert.fail("object should not be able to persist");
            }
            catch (Exception ex)
            {
                Assert.assertTrue(true);
            }
            tx.close();
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

            ITransaction tx = connector.createTransaction();
            Collection<Class> dbClasses = new ArrayList<>();
            dbClasses.add(ThreeColumnEntity.class);
            connector.getDbGate().patchDataBase(tx,dbClasses,true);
            tx.commit();
            tx.close();

            int id = 34;
            tx = connector.createTransaction();
            ThreeColumnEntity columnEntity = createThreeColumnEntity(id);
            columnEntity.setName(sb.toString());
            try
            {
                columnEntity.persist(tx);
                Assert.fail("Object should not be able to persist");
            }
            catch (Exception ex)
            {
                //eat it
            }
            tx.close();


            tx = connector.createTransaction();
            dbClasses = new ArrayList<>();
            dbClasses.add(ThreeColumnTypeDifferentEntity.class);
            connector.getDbGate().patchDataBase(tx,dbClasses,false);
            tx.commit();
            tx.close();

            id = 35;
            tx = connector.createTransaction();
            columnEntity = createThreeColumnEntity(id);
            columnEntity.setName(sb.toString());
            columnEntity.persist(tx);
            tx.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private FourColumnEntity loadFourColumnEntityWithId(ITransaction tx,int id) throws Exception
    {
        FourColumnEntity loadedEntity = null;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from table_change_test_entity where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadedEntity = new FourColumnEntity();
            loadedEntity.retrieve(rs,tx);
        }
        rs.close();
        ps.close();
        tx.close();

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
        finalizeDb(dbName);
    }
}