package dbgate.ermanagement;

import dbgate.EntityStatus;
import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.ermanagement.support.persistant.inheritancetest.*;
import junit.framework.Assert;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Aug 29, 2010
 * Time: 6:40:58 PM
 */
public class ErManagementInheritancePersistTests
{
    private static DBConnector connector;

    public static final int TYPE_ANNOTATION = 1;
    public static final int TYPE_FIELD = 2;
    public static final int TYPE_EXTERNAL = 3;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(ErManagementInheritancePersistTests.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-inheritance-persist;create=true");

            String sql = "Create table inheritance_test_super (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table inheritance_test_suba (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname_a Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table inheritance_test_subb (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname_b Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DBConnector("jdbc:derby:memory:unit-testing-inheritance-persist;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);

            ERLayer.getSharedInstance().getConfig().setAutoTrackChanges(false);
            ERLayer.getSharedInstance().getConfig().setCheckVersion(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(ErManagementInheritancePersistTests.class.getName()).severe("Exception during database startup.");
        }
    }

    private void registerForExternal()
    {
        Class objType = InheritanceTestSuperEntityExt.class;
        ERLayer.getSharedInstance().registerEntity(objType,InheritanceTestExtFactory.getTableNames(objType)
                ,InheritanceTestExtFactory.getFieldInfo(objType));

        objType = InheritanceTestSubEntityAExt.class;
        ERLayer.getSharedInstance().registerEntity(objType,InheritanceTestExtFactory.getTableNames(objType)
                ,InheritanceTestExtFactory.getFieldInfo(objType));

        objType = InheritanceTestSubEntityBExt.class;
        ERLayer.getSharedInstance().registerEntity(objType,InheritanceTestExtFactory.getTableNames(objType)
                ,InheritanceTestExtFactory.getFieldInfo(objType));
    }

    @Before
    public void beforeEach()
    {

        ERLayer.getSharedInstance().clearCache();
    }

    @Test
    public void inheritance_insert_withAllModesWithBothSubClasses_shouldEqualWhenLoaded()
    {
        try
        {
            int types[] = new int[]{TYPE_ANNOTATION,TYPE_EXTERNAL,TYPE_FIELD};
            int idAs[] = new int[]{35,45,55};
            int idBs[] = new int[]{36,46,56};

            for (int i = 0; i < types.length; i++)
            {
                int type = types[i];
                int idA = idAs[i];
                int idB = idBs[i];

                switch (type)
                {
                    case TYPE_ANNOTATION:
                        System.out.println("inheritance_insert_withAllModesWithBothSubClasses_shouldEqualWhenLoaded With annotations");
                        break;
                    case TYPE_EXTERNAL:
                        System.out.println("inheritance_insert_withAllModesWithBothSubClasses_shouldEqualWhenLoaded With externals");
                        break;
                    case TYPE_FIELD:
                        System.out.println("inheritance_insert_withAllModesWithBothSubClasses_shouldEqualWhenLoaded With fields");
                        break;
                }

                Connection connection = connector.getConnection();

                ERLayer.getSharedInstance().clearCache();
                if (type == TYPE_EXTERNAL)
                {
                    registerForExternal();
                }

                IInheritanceTestSuperEntity entityA = createObjectWithDataTypeA(idA,type);
                IInheritanceTestSuperEntity entityB = createObjectWithDataTypeB(idB,type);
                entityA.persist(connection);
                entityB.persist(connection);
                connection.commit();
                connection.close();

                connection = connector.getConnection();
                IInheritanceTestSuperEntity loadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSuperEntity loadedEntityB = createObjectEmptyTypeB(type);
                loadEntityWithTypeA(connection,loadedEntityA,idA);
                loadEntityWithTypeB(connection,loadedEntityB,idB);
                connection.close();

                boolean compareResult = compareEntities(entityA,loadedEntityA);
                Assert.assertTrue(compareResult);
                compareResult = compareEntities(entityB,loadedEntityB);
                Assert.assertTrue(compareResult);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void inheritance_update_withAllModesWithBothSubClasses_shouldEqualWhenLoaded()
    {
        try
        {
            int types[] = new int[]{TYPE_ANNOTATION,TYPE_EXTERNAL,TYPE_FIELD};
            int idAs[] = new int[]{35,45,55};
            int idBs[] = new int[]{36,46,56};

            for (int i = 0; i < types.length; i++)
            {
                int type = types[i];
                int idA = idAs[i];
                int idB = idBs[i];

                switch (type)
                {
                    case TYPE_ANNOTATION:
                        System.out.println("inheritance_update_withAllModesWithBothSubClasses_shouldEqualWhenLoaded With annotations");
                        break;
                    case TYPE_EXTERNAL:
                        System.out.println("inheritance_update_withAllModesWithBothSubClasses_shouldEqualWhenLoaded With externals");
                        break;
                    case TYPE_FIELD:
                        System.out.println("inheritance_update_withAllModesWithBothSubClasses_shouldEqualWhenLoaded With fields");
                        break;
                }

                Connection connection = connector.getConnection();

                ERLayer.getSharedInstance().clearCache();
                if (type == TYPE_EXTERNAL)
                {
                    registerForExternal();
                }

                IInheritanceTestSuperEntity entityA = createObjectWithDataTypeA(idA,type);
                IInheritanceTestSuperEntity entityB = createObjectWithDataTypeB(idB,type);
                entityA.persist(connection);
                entityB.persist(connection);
                connection.commit();
                connection.close();

                connection = connector.getConnection();
                IInheritanceTestSubEntityA loadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSubEntityB loadedEntityB = createObjectEmptyTypeB(type);
                loadEntityWithTypeA(connection,loadedEntityA,idA);
                loadEntityWithTypeB(connection,loadedEntityB,idB);
                connection.close();

                loadedEntityA.setName("typeA-changed-name");
                loadedEntityA.setNameA("changed-nameA");
                loadedEntityA.setStatus(EntityStatus.MODIFIED);
                loadedEntityB.setName("typeB-changed-name");
                loadedEntityB.setNameB("changed-nameB");
                loadedEntityB.setStatus(EntityStatus.MODIFIED);

                connection = connector.getConnection();
                loadedEntityA.persist(connection);
                loadedEntityB.persist(connection);
                connection.close();

                connection = connector.getConnection();
                IInheritanceTestSubEntityA reLoadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSubEntityB reLoadedEntityB = createObjectEmptyTypeB(type);
                loadEntityWithTypeA(connection,reLoadedEntityA,idA);
                loadEntityWithTypeB(connection,reLoadedEntityB,idB);
                connection.close();
                connection.close();

                boolean compareResult = compareEntities(loadedEntityA,reLoadedEntityA);
                Assert.assertTrue(compareResult);
                compareResult = compareEntities(loadedEntityB,reLoadedEntityB);
                Assert.assertTrue(compareResult);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void inheritance_delete_withAllModesWithBothSubClasses_shouldDelete()
    {
        try
        {
            int types[] = new int[]{TYPE_ANNOTATION,TYPE_EXTERNAL,TYPE_FIELD};
            int idAs[] = new int[]{35,45,55};
            int idBs[] = new int[]{36,46,56};

            for (int i = 0; i < types.length; i++)
            {
                int type = types[i];
                int idA = idAs[i];
                int idB = idBs[i];

                switch (type)
                {
                    case TYPE_ANNOTATION:
                        System.out.println("inheritance_delete_withAllModesWithBothSubClasses_shouldDelete With annotations");
                        break;
                    case TYPE_EXTERNAL:
                        System.out.println("inheritance_delete_withAllModesWithBothSubClasses_shouldDelete With externals");
                        break;
                    case TYPE_FIELD:
                        System.out.println("inheritance_delete_withAllModesWithBothSubClasses_shouldDelete With fields");
                        break;
                }

                Connection connection = connector.getConnection();

                ERLayer.getSharedInstance().clearCache();
                if (type == TYPE_EXTERNAL)
                {
                    registerForExternal();
                }

                IInheritanceTestSuperEntity entityA = createObjectWithDataTypeA(idA,type);
                IInheritanceTestSuperEntity entityB = createObjectWithDataTypeB(idB,type);
                entityA.persist(connection);
                entityB.persist(connection);
                connection.commit();
                connection.close();

                connection = connector.getConnection();
                IInheritanceTestSubEntityA loadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSubEntityB loadedEntityB = createObjectEmptyTypeB(type);
                loadEntityWithTypeA(connection,loadedEntityA,idA);
                loadEntityWithTypeB(connection,loadedEntityB,idB);
                connection.close();

                loadedEntityA.setName("typeA-changed-name");
                loadedEntityA.setNameA("changed-nameA");
                loadedEntityA.setStatus(EntityStatus.DELETED);
                loadedEntityB.setName("typeB-changed-name");
                loadedEntityB.setNameB("changed-nameB");
                loadedEntityB.setStatus(EntityStatus.DELETED);

                connection = connector.getConnection();
                loadedEntityA.persist(connection);
                loadedEntityB.persist(connection);
                connection.close();

                connection = connector.getConnection();
                IInheritanceTestSubEntityA reLoadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSubEntityB reLoadedEntityB = createObjectEmptyTypeB(type);
                boolean reLoadedA = loadEntityWithTypeA(connection,reLoadedEntityA,idA);
                boolean existesSuperA = existsSuper(connection,idA);
                boolean existesSubA = existsSubA(connection,idA);
                boolean reLoadedB = loadEntityWithTypeB(connection,reLoadedEntityB,idB);
                boolean existesSuperB = existsSuper(connection,idB);
                boolean existesSubB = existsSubB(connection,idB);
                connection.close();
                connection.close();

                Assert.assertFalse(reLoadedA);
                Assert.assertFalse(existesSuperA);
                Assert.assertFalse(existesSubA);
                Assert.assertFalse(reLoadedB);
                Assert.assertFalse(existesSuperB);
                Assert.assertFalse(existesSubB);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadEntityWithTypeA(Connection connection, IInheritanceTestSuperEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from inheritance_test_suba where id_col = ?");
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

    private boolean loadEntityWithTypeB(Connection connection, IInheritanceTestSuperEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from inheritance_test_subb where id_col = ?");
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

    private boolean existsSuper(Connection connection,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = connection.prepareStatement("select * from inheritance_test_super where id_col = ?");
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

    private boolean existsSubA(Connection connection,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = connection.prepareStatement("select * from inheritance_test_suba where id_col = ?");
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

    private boolean existsSubB(Connection connection,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = connection.prepareStatement("select * from inheritance_test_subb where id_col = ?");
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

    private IInheritanceTestSubEntityA createObjectWithDataTypeA(int id,int type)
    {
        IInheritanceTestSubEntityA entity;
        entity = (type == TYPE_ANNOTATION)?new InheritanceTestSubEntityAAnnotations()
                                          :(type==TYPE_FIELD)?new InheritanceTestSubEntityAFields()
                                                             : new InheritanceTestSubEntityAExt();
        entity.setIdCol(id);
        entity.setName("typeA-name");
        entity.setNameA("typeA-nameA");

        return entity;
    }

    private IInheritanceTestSubEntityB createObjectWithDataTypeB(int id,int type)
    {
        IInheritanceTestSubEntityB entity;
        entity = (type == TYPE_ANNOTATION)?new InheritanceTestSubEntityBAnnotations()
                                          :(type==TYPE_FIELD)?new InheritanceTestSubEntityBFields()
                                                             : new InheritanceTestSubEntityBExt();
        entity.setIdCol(id);
        entity.setName("typeB-name");
        entity.setNameB("typeB-nameB");

        return entity;
    }

    private IInheritanceTestSubEntityA createObjectEmptyTypeA(int type)
    {
        IInheritanceTestSubEntityA entity;
        entity = (type == TYPE_ANNOTATION)?new InheritanceTestSubEntityAAnnotations()
                                          :(type==TYPE_FIELD)?new InheritanceTestSubEntityAFields()
                                                             : new InheritanceTestSubEntityAExt();
        return entity;
    }

    private IInheritanceTestSubEntityB createObjectEmptyTypeB(int type)
    {
        IInheritanceTestSubEntityB entity;
        entity = (type == TYPE_ANNOTATION)?new InheritanceTestSubEntityBAnnotations()
                                          :(type==TYPE_FIELD)?new InheritanceTestSubEntityBFields()
                                                             : new InheritanceTestSubEntityBExt();
        return entity;
    }

    private boolean compareEntities(IInheritanceTestSuperEntity entityA, IInheritanceTestSuperEntity entityB)
    {
        boolean result = entityA.getIdCol() == entityB.getIdCol();
        result &= entityA.getName().equals(entityB.getName());

        if (entityA instanceof IInheritanceTestSubEntityA
                && entityB instanceof IInheritanceTestSubEntityA)
        {
            result &= ((IInheritanceTestSubEntityA) entityA).getNameA().equals(((IInheritanceTestSubEntityA)entityB).getNameA());
        }
        else if (entityA instanceof IInheritanceTestSubEntityB
                && entityB instanceof IInheritanceTestSubEntityB)
        {
            result &= ((IInheritanceTestSubEntityB) entityA).getNameB().equals(((IInheritanceTestSubEntityB)entityB).getNameB());
        }
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
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-inheritance-persist;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM inheritance_test_super");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM inheritance_test_suba");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM inheritance_test_subb");
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
        Logger.getLogger(ErManagementInheritancePersistTests.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-inheritance-persist;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-inheritance-persist").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}