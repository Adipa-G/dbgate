package dbgate;

import dbgate.support.persistant.crossreference.CrossReferenceTestOne2ManyEntity;
import dbgate.support.persistant.crossreference.CrossReferenceTestOne2OneEntity;
import dbgate.support.persistant.crossreference.CrossReferenceTestRootEntity;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateCrossReferenceTest
{
    private static DefaultTransactionFactory connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(DbGateCrossReferenceTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-cross-reference;create=true");

            String sql = "Create table cross_reference_test_root (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table cross_reference_test_one2many (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tindex_no Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col,index_no))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table cross_reference_test_one2one (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DefaultTransactionFactory("jdbc:derby:memory:unit-testing-cross-reference;","org.apache.derby.jdbc.EmbeddedDriver",
                                                      DefaultTransactionFactory.DB_DERBY);

            connector.getDbGate().getConfig().setAutoTrackChanges(false);
            connector.getDbGate().getConfig().setCheckVersion(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(DbGateCrossReferenceTest.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
    }

    @Test
    public void crossReference_persistWithOne2OneChild_WithCrossReference_loadedShouldBeSameAsPersisted()
    {
        try
        {
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            CrossReferenceTestRootEntity entity = new CrossReferenceTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            CrossReferenceTestOne2OneEntity one2OneEntity = new CrossReferenceTestOne2OneEntity();
            one2OneEntity.setIdCol(id);
            one2OneEntity.setName("Child-Entity");
            one2OneEntity.setRootEntity(entity);
            entity.setOne2OneEntity(one2OneEntity);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            CrossReferenceTestRootEntity loadedEntity = new CrossReferenceTestRootEntity();
            loadEntityWithId(tx,loadedEntity,id);
            tx.close();

            Assert.assertNotNull(loadedEntity);
            Assert.assertNotNull(loadedEntity.getOne2OneEntity());
            Assert.assertNotNull(loadedEntity.getOne2OneEntity().getRootEntity());
            Assert.assertTrue(loadedEntity == loadedEntity.getOne2OneEntity().getRootEntity());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void crossReference_persistWithOne2ManyChild_WithCrossReference_loadedShouldBeSameAsPersisted()
    {
        try
        {
            connector.getDbGate().getConfig().setAutoTrackChanges(true);
            ITransaction tx = connector.createTransaction();

            int id = 45;
            CrossReferenceTestRootEntity entity = new CrossReferenceTestRootEntity();
            entity.setIdCol(id);
            entity.setName("Org-Name");
            CrossReferenceTestOne2ManyEntity one2ManyEntity = new CrossReferenceTestOne2ManyEntity();
            one2ManyEntity.setIdCol(id);
            one2ManyEntity.setIndexNo(1);
            one2ManyEntity.setName("Child-Entity");
            one2ManyEntity.setRootEntity(entity);
            entity.getOne2ManyEntities().add(one2ManyEntity);
            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            CrossReferenceTestRootEntity loadedEntity = new CrossReferenceTestRootEntity();
            loadEntityWithId(tx,loadedEntity,id);
            tx.close();

            Assert.assertNotNull(loadedEntity);
            Assert.assertTrue(loadedEntity.getOne2ManyEntities().size() == 1);
            Assert.assertNotNull(loadedEntity.getOne2ManyEntities().iterator().next().getRootEntity());
            Assert.assertTrue(loadedEntity == loadedEntity.getOne2ManyEntities().iterator().next().getRootEntity());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadEntityWithId(ITransaction tx, CrossReferenceTestRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from cross_reference_test_root where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadEntity.retrieve(rs,tx);
            loaded = true;
        }
        rs.close();
        ps.close();

        return loaded;
    }

    @After
    public void afterEach()
    {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-cross-reference;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM cross_reference_test_root");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM cross_reference_test_one2many");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM cross_reference_test_one2one");
            ps.execute();

            con.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void after()
    {
        Logger.getLogger(DbGateCrossReferenceTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-cross-reference;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-cross-reference").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}