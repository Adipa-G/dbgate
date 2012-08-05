package dbgate.ermanagement;

import dbgate.DBClassStatus;
import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.ermanagement.support.persistant.treetest.*;
import junit.framework.Assert;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Aug 29, 2010
 * Time: 6:40:58 PM
 */
public class ErManagementTreePersistTests
{
    public static final int TYPE_ANNOTATION = 1;
    public static final int TYPE_FIELD = 2;
    public static final int TYPE_EXTERNAL = 3;

    private static DBConnector connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(ErManagementTreePersistTests.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-tree-persist;create=true");

            String sql = "Create table tree_test_root (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table tree_test_one2many (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tindex_no Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col,index_no))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table tree_test_one2one (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DBConnector("jdbc:derby:memory:unit-testing-tree-persist;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);

            ERLayer.getSharedInstance().getConfig().setAutoTrackChanges(false);
            ERLayer.getSharedInstance().getConfig().setCheckVersion(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(ErManagementTreePersistTests.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        ERLayer.getSharedInstance().clearCache();
    }

    private void registerForExternal()
    {
        Class objType = TreeTestRootEntityExt.class;
        ERLayer.getSharedInstance().registerTable(objType,TreeTestExtFactory.getTableNames(objType));
        ERLayer.getSharedInstance().registerFields(objType,TreeTestExtFactory.getFieldInfo(objType));

        objType = TreeTestOne2OneEntityExt.class;
        ERLayer.getSharedInstance().registerTable(objType,TreeTestExtFactory.getTableNames(objType));
        ERLayer.getSharedInstance().registerFields(objType,TreeTestExtFactory.getFieldInfo(objType));

        objType = TreeTestOne2ManyEntityExt.class;
        ERLayer.getSharedInstance().registerTable(objType,TreeTestExtFactory.getTableNames(objType));
        ERLayer.getSharedInstance().registerFields(objType,TreeTestExtFactory.getFieldInfo(objType));
    }

    @Test
    public void treePersist_insert_withAnnotationsDifferentTypeOfChildren_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 35;
            ITreeTestRootEntity rootEntity = createFullObjectTree(id,TYPE_ANNOTATION);
            rootEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity loadedEntity = new TreeTestRootEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            boolean compareResult = compareEntities(rootEntity,loadedEntity);
            Assert.assertTrue(compareResult);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void treePersist_insert_withFieldsDifferentTypeOfChildren_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 35;
            ITreeTestRootEntity rootEntity = createFullObjectTree(id,TYPE_FIELD);
            rootEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity loadedEntity = new TreeTestRootEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            boolean compareResult = compareEntities(rootEntity,loadedEntity);
            Assert.assertTrue(compareResult);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void treePersist_insert_withExtsDifferentTypeOfChildren_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            registerForExternal();

            int id = 35;
            ITreeTestRootEntity rootEntity = createFullObjectTree(id,TYPE_EXTERNAL);
            rootEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity loadedEntity = new TreeTestRootEntityExt();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            boolean compareResult = compareEntities(rootEntity,loadedEntity);
            Assert.assertTrue(compareResult);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void treePersist_update_withAnnotationsDifferentTypeOfChildren_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 35;
            ITreeTestRootEntity rootEntity = createFullObjectTree(id,TYPE_ANNOTATION);
            rootEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity loadedEntity = new TreeTestRootEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            loadedEntity.setName("changed-name");
            loadedEntity.setStatus(DBClassStatus.MODIFIED);
            loadedEntity.getOne2OneEntity().setName("changed-one2one");
            loadedEntity.getOne2OneEntity().setStatus(DBClassStatus.MODIFIED);

            ITreeTestOne2ManyEntity one2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            one2ManyEntity.setName("changed-one2many");
            one2ManyEntity.setStatus(DBClassStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity reLoadedEntity = new TreeTestRootEntityAnnotations();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            boolean compareResult = compareEntities(loadedEntity,reLoadedEntity);
            Assert.assertTrue(compareResult);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void treePersist_update_withFieldsDifferentTypeOfChildren_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 35;
            ITreeTestRootEntity rootEntity = createFullObjectTree(id,TYPE_FIELD);
            rootEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity loadedEntity = new TreeTestRootEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            loadedEntity.setName("changed-name");
            loadedEntity.setStatus(DBClassStatus.MODIFIED);
            loadedEntity.getOne2OneEntity().setName("changed-one2one");
            loadedEntity.getOne2OneEntity().setStatus(DBClassStatus.MODIFIED);

            ITreeTestOne2ManyEntity one2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            one2ManyEntity.setName("changed-one2many");
            one2ManyEntity.setStatus(DBClassStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity reLoadedEntity = new TreeTestRootEntityFields();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            boolean compareResult = compareEntities(loadedEntity,reLoadedEntity);
            Assert.assertTrue(compareResult);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void treePersist_update_withExtsDifferentTypeOfChildren_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            registerForExternal();

            int id = 35;
            ITreeTestRootEntity rootEntity = createFullObjectTree(id,TYPE_EXTERNAL);
            rootEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity loadedEntity = new TreeTestRootEntityExt();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            loadedEntity.setName("changed-name");
            loadedEntity.setStatus(DBClassStatus.MODIFIED);
            loadedEntity.getOne2OneEntity().setName("changed-one2one");
            loadedEntity.getOne2OneEntity().setStatus(DBClassStatus.MODIFIED);

            ITreeTestOne2ManyEntity one2ManyEntity = loadedEntity.getOne2ManyEntities().iterator().next();
            one2ManyEntity.setName("changed-one2many");
            one2ManyEntity.setStatus(DBClassStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity reLoadedEntity = new TreeTestRootEntityExt();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            boolean compareResult = compareEntities(loadedEntity,reLoadedEntity);
            Assert.assertTrue(compareResult);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void treePersist_delete_withAnnotationsDifferentTypeOfChildren_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 35;
            ITreeTestRootEntity rootEntity = createFullObjectTree(id,TYPE_ANNOTATION);
            rootEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity loadedEntity = new TreeTestRootEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            loadedEntity.setName("changed-name");
            loadedEntity.setStatus(DBClassStatus.DELETED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity reLoadedEntity = new TreeTestRootEntityAnnotations();
            boolean loaded = loadEntityWithId(connection,reLoadedEntity,id);
            boolean existsOne2one = existsOne2OneChild(connection,id);
            boolean existsOne2many = existsOne2ManyChild(connection,id);
            connection.close();

            Assert.assertFalse(loaded);
            Assert.assertFalse(existsOne2one);
            Assert.assertFalse(existsOne2many);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void treePersist_delete_withFieldsDifferentTypeOfChildren_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = 35;
            ITreeTestRootEntity rootEntity = createFullObjectTree(id,TYPE_FIELD);
            rootEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity loadedEntity = new TreeTestRootEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            loadedEntity.setName("changed-name");
            loadedEntity.setStatus(DBClassStatus.DELETED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity reLoadedEntity = new TreeTestRootEntityFields();
            boolean loaded = loadEntityWithId(connection,reLoadedEntity,id);
            boolean existsOne2one = existsOne2OneChild(connection,id);
            boolean existsOne2many = existsOne2ManyChild(connection,id);
            connection.close();

            Assert.assertFalse(loaded);
            Assert.assertFalse(existsOne2one);
            Assert.assertFalse(existsOne2many);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void treePersist_delete_withExtsDifferentTypeOfChildren_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            registerForExternal();

            int id = 35;
            ITreeTestRootEntity rootEntity = createFullObjectTree(id,TYPE_EXTERNAL);
            rootEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity loadedEntity = new TreeTestRootEntityExt();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            loadedEntity.setName("changed-name");
            loadedEntity.setStatus(DBClassStatus.DELETED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.close();

            connection = connector.getConnection();
            ITreeTestRootEntity reLoadedEntity = new TreeTestRootEntityExt();
            boolean loaded = loadEntityWithId(connection,reLoadedEntity,id);
            boolean existsOne2one = existsOne2OneChild(connection,id);
            boolean existsOne2many = existsOne2ManyChild(connection,id);
            connection.close();

            Assert.assertFalse(loaded);
            Assert.assertFalse(existsOne2one);
            Assert.assertFalse(existsOne2many);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadEntityWithId(Connection connection, ITreeTestRootEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from tree_test_root where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            loadEntity.retrieve(rs,connection);
            loaded = true;
        }
        rs.close();
        ps.close();

        return loaded;
    }

    private boolean existsOne2OneChild(Connection connection,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = connection.prepareStatement("select * from tree_test_one2one where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            exists = true;
        }
        rs.close();
        ps.close();

        return exists;
    }

    private boolean existsOne2ManyChild(Connection connection,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = connection.prepareStatement("select * from tree_test_one2many where id_col = ?");
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            exists = true;
        }
        rs.close();
        ps.close();

        return exists;
    }

    private ITreeTestRootEntity createFullObjectTree(int id,int type)
    {
        ITreeTestRootEntity entity;
        entity = (type == TYPE_ANNOTATION)?new TreeTestRootEntityAnnotations()
                                          :(type==TYPE_FIELD)?new TreeTestRootEntityFields()
                                                             :new TreeTestRootEntityExt();
        entity.setIdCol(id);
        entity.setName("root");

        ITreeTestOne2OneEntity one2OneEntity;
        one2OneEntity = (type == TYPE_ANNOTATION)?new TreeTestOne2OneEntityAnnotations()
                                          :(type==TYPE_FIELD)?new TreeTestOne2OneEntityFields()
                                                             :new TreeTestOne2OneEntityExt();
        one2OneEntity.setIdCol(id);
        one2OneEntity.setName("one2one");
        entity.setOne2OneEntity(one2OneEntity);

        ITreeTestOne2ManyEntity one2ManyEntity;
        one2ManyEntity = (type == TYPE_ANNOTATION)?new TreeTestOne2ManyEntityAnnotations()
                                          :(type==TYPE_FIELD)?new TreeTestOne2ManyEntityFields()
                                                             :new TreeTestOne2ManyEntityExt();
        one2ManyEntity.setIdCol(id);
        one2ManyEntity.setIndexNo(0);
        one2ManyEntity.setName("one2many");
        entity.setOne2ManyEntities(new ArrayList<ITreeTestOne2ManyEntity>());
        entity.getOne2ManyEntities().add(one2ManyEntity);

        return entity;
    }

    private boolean compareEntities(ITreeTestRootEntity rootA, ITreeTestRootEntity rootB)
    {
        boolean result = rootA.getIdCol() == rootB.getIdCol();
        result &= rootA.getName().equals(rootB.getName());

        if (rootA.getOne2OneEntity() != null
                && rootB.getOne2OneEntity() != null)
        {
            result &= rootA.getOne2OneEntity().getIdCol() == rootB.getOne2OneEntity().getIdCol();
            result &= rootA.getOne2OneEntity().getName().equals(rootB.getOne2OneEntity().getName());
        }
        else if (rootA.getOne2OneEntity() == null
                && rootB.getOne2OneEntity() == null){}
        else
        {
            return false;
        }
        if (!result)
        {
            return false;
        }

        if (rootA.getOne2ManyEntities() != null
                && rootB.getOne2ManyEntities() != null)
        {
            if (rootA.getOne2ManyEntities().size() != rootB.getOne2ManyEntities().size())
            {
                return false;
            }
            for (ITreeTestOne2ManyEntity one2ManyEntityA : rootA.getOne2ManyEntities())
            {
                boolean found = false;
                for (ITreeTestOne2ManyEntity one2ManyEntityB : rootB.getOne2ManyEntities())
                {
                    found = one2ManyEntityA.getIdCol() == one2ManyEntityB.getIdCol();
                    found &= one2ManyEntityA.getIndexNo() == one2ManyEntityB.getIndexNo();
                    found &= one2ManyEntityA.getName().equals(one2ManyEntityB.getName());
                }
                if (!found)
                {
                    return false;
                }
            }
        }
        else if (rootA.getOne2ManyEntities() == null
                && rootB.getOne2ManyEntities() == null){}
        else
        {
            return false;
        }

        return result;
    }

    @After
    public void afterEach()
    {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-tree-persist;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM tree_test_root");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM tree_test_one2many");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM tree_test_one2one");
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
        Logger.getLogger(ErManagementTreePersistTests.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-tree-persist;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-tree-persist").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}