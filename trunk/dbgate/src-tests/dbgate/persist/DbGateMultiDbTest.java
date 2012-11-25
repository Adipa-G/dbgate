package dbgate.persist;

import dbgate.AbstractDbGateTestBase;
import dbgate.DefaultTransactionFactory;
import dbgate.ITransaction;
import dbgate.UpdateStrategy;
import dbgate.persist.support.multidb.MultiDbEntity;
import org.junit.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateMultiDbTest extends AbstractDbGateTestBase
{
    private static final String db1Name = "unit-testing-multi-db-1";
    private static final String db2Name = "unit-testing-multi-db-2";

    private static DefaultTransactionFactory connector2;

    @BeforeClass
    public static void before()
    {
        testClass = DbGateMultiDbTest.class;
        beginInit(db1Name);
        beginInit(db2Name);

        try
        {
            connector2 = new DefaultTransactionFactory(String.format("jdbc:derby:memory:%s;",db2Name)
                    ,driverName,DefaultTransactionFactory.DB_DERBY);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Logger.getLogger(testClass.getName()).severe(String.format("Exception during database %s startup.",db2Name));
        }

        String sql = "Create table multi_db_test_root (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(100) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,db1Name);
        createTableFromSql(sql,db2Name);

        endInit(db1Name);
        endInit(db2Name);
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
        connector.getDbGate().getConfig().setDefaultUpdateStrategy(UpdateStrategy.All_COLUMNS);
    }

    @Test
    public void multipleDatabaseSupport_persistTwoEntitiesInTwoDbs_AndFetchThem_ShouldFetch()
    {
        try
        {
            ITransaction txDb1 = connector.createTransaction();
            int id = 35;
            MultiDbEntity db1Entity = new MultiDbEntity();
            db1Entity.setIdCol(id);
            db1Entity.setName(db1Name);
            db1Entity.persist(txDb1);
            txDb1.commit();
            txDb1.close();

            ITransaction txDb2 = connector2.createTransaction();
            MultiDbEntity db2Entity = new MultiDbEntity();
            db2Entity.setIdCol(id);
            db2Entity.setName(db2Name);
            db2Entity.persist(txDb2);
            txDb2.commit();
            txDb2.close();

            txDb1 = connector.createTransaction();
            MultiDbEntity db1LoadedEntity = new MultiDbEntity();
            loadWithId(txDb1,db1LoadedEntity,id);
            Assert.assertEquals(db1LoadedEntity.getName(),db1Name);
            txDb1.close();

            txDb2 = connector2.createTransaction();
            MultiDbEntity db2LoadedEntity = new MultiDbEntity();
            loadWithId(txDb2,db2LoadedEntity,id);
            Assert.assertEquals(db2LoadedEntity.getName(),db2Name);
            txDb2.close();
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadWithId(ITransaction tx, MultiDbEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from multi_db_test_root where id_col = ?");
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
        cleanupDb(db1Name);
        cleanupDb(db2Name);
    }

    @AfterClass
    public static void after()
    {
        finalizeDb(db1Name);
        finalizeDb(db2Name);
    }
}
