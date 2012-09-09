package dbgate;

import dbgate.support.persistant.inheritancetest.*;
import junit.framework.Assert;
import org.junit.*;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Aug 29, 2010
 * Time: 6:40:58 PM
 */
public class DbGateInheritancePersistTests extends AbstractDbGateTestBase
{
    private static final String dbName = "unit-testing-inheritance-persist";

    public static final int TYPE_ANNOTATION = 1;
    public static final int TYPE_FIELD = 2;
    public static final int TYPE_EXTERNAL = 3;

    @BeforeClass
    public static void before()
    {
        testClass = DbGateInheritancePersistTests.class;
        beginInit(dbName);

        String sql = "Create table inheritance_test_super (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        sql = "Create table inheritance_test_suba (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname_a Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        sql = "Create table inheritance_test_subb (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname_b Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);
        endInit(dbName);
    }

    private void registerForExternal()
    {
        Class objType = InheritanceTestSuperEntityExt.class;
        connector.getDbGate().registerEntity(objType,InheritanceTestExtFactory.getTableInfo(objType)
                ,InheritanceTestExtFactory.getFieldInfo(objType));

        objType = InheritanceTestSubEntityAExt.class;
        connector.getDbGate().registerEntity(objType,InheritanceTestExtFactory.getTableInfo(objType)
                ,InheritanceTestExtFactory.getFieldInfo(objType));

        objType = InheritanceTestSubEntityBExt.class;
        connector.getDbGate().registerEntity(objType,InheritanceTestExtFactory.getTableInfo(objType)
                ,InheritanceTestExtFactory.getFieldInfo(objType));
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();

        connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.MANUAL);
        connector.getDbGate().getConfig().setDefaultVerifyOnWriteStrategy(VerifyOnWriteStrategy.DO_NOT_VERIFY);
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

                ITransaction tx = connector.createTransaction();

                connector.getDbGate().clearCache();
                if (type == TYPE_EXTERNAL)
                {
                    registerForExternal();
                }

                IInheritanceTestSuperEntity entityA = createObjectWithDataTypeA(idA,type);
                IInheritanceTestSuperEntity entityB = createObjectWithDataTypeB(idB,type);
                entityA.persist(tx);
                entityB.persist(tx);
                tx.commit();
                tx.close();

                tx = connector.createTransaction();
                IInheritanceTestSuperEntity loadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSuperEntity loadedEntityB = createObjectEmptyTypeB(type);
                loadEntityWithTypeA(tx,loadedEntityA,idA);
                loadEntityWithTypeB(tx, loadedEntityB, idB);
                tx.close();

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

                ITransaction tx = connector.createTransaction();

                connector.getDbGate().clearCache();
                if (type == TYPE_EXTERNAL)
                {
                    registerForExternal();
                }

                IInheritanceTestSuperEntity entityA = createObjectWithDataTypeA(idA,type);
                IInheritanceTestSuperEntity entityB = createObjectWithDataTypeB(idB,type);
                entityA.persist(tx);
                entityB.persist(tx);
                tx.commit();
                tx.close();

                tx = connector.createTransaction();
                IInheritanceTestSubEntityA loadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSubEntityB loadedEntityB = createObjectEmptyTypeB(type);
                loadEntityWithTypeA(tx, loadedEntityA,idA);
                loadEntityWithTypeB(tx, loadedEntityB, idB);
                tx.close();

                loadedEntityA.setName("typeA-changed-name");
                loadedEntityA.setNameA("changed-nameA");
                loadedEntityA.setStatus(EntityStatus.MODIFIED);
                loadedEntityB.setName("typeB-changed-name");
                loadedEntityB.setNameB("changed-nameB");
                loadedEntityB.setStatus(EntityStatus.MODIFIED);

                tx = connector.createTransaction();
                loadedEntityA.persist(tx);
                loadedEntityB.persist(tx);
                tx.close();

                tx = connector.createTransaction();
                IInheritanceTestSubEntityA reLoadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSubEntityB reLoadedEntityB = createObjectEmptyTypeB(type);
                loadEntityWithTypeA(tx, reLoadedEntityA, idA);
                loadEntityWithTypeB(tx,reLoadedEntityB,idB);
                tx.close();
                tx.close();

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

                ITransaction tx = connector.createTransaction();

                connector.getDbGate().clearCache();
                if (type == TYPE_EXTERNAL)
                {
                    registerForExternal();
                }

                IInheritanceTestSuperEntity entityA = createObjectWithDataTypeA(idA,type);
                IInheritanceTestSuperEntity entityB = createObjectWithDataTypeB(idB,type);
                entityA.persist(tx);
                entityB.persist(tx);
                tx.commit();
                tx.close();

                tx = connector.createTransaction();
                IInheritanceTestSubEntityA loadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSubEntityB loadedEntityB = createObjectEmptyTypeB(type);
                loadEntityWithTypeA(tx, loadedEntityA,idA);
                loadEntityWithTypeB(tx, loadedEntityB, idB);
                tx.close();

                loadedEntityA.setName("typeA-changed-name");
                loadedEntityA.setNameA("changed-nameA");
                loadedEntityA.setStatus(EntityStatus.DELETED);
                loadedEntityB.setName("typeB-changed-name");
                loadedEntityB.setNameB("changed-nameB");
                loadedEntityB.setStatus(EntityStatus.DELETED);

                tx = connector.createTransaction();
                loadedEntityA.persist(tx);
                loadedEntityB.persist(tx);
                tx.close();

                tx = connector.createTransaction();
                IInheritanceTestSubEntityA reLoadedEntityA = createObjectEmptyTypeA(type);
                IInheritanceTestSubEntityB reLoadedEntityB = createObjectEmptyTypeB(type);
                boolean reLoadedA = loadEntityWithTypeA(tx,reLoadedEntityA,idA);
                boolean existesSuperA = existsSuper(tx, idA);
                boolean existesSubA = existsSubA(tx, idA);
                boolean reLoadedB = loadEntityWithTypeB(tx, reLoadedEntityB, idB);
                boolean existesSuperB = existsSuper(tx,idB);
                boolean existesSubB = existsSubB(tx, idB);
                tx.close();
                tx.close();

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

    private boolean loadEntityWithTypeA(ITransaction tx, IInheritanceTestSuperEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from inheritance_test_suba where id_col = ?");
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

    private boolean loadEntityWithTypeB(ITransaction tx, IInheritanceTestSuperEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from inheritance_test_subb where id_col = ?");
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

    private boolean existsSuper(ITransaction tx,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from inheritance_test_super where id_col = ?");
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

    private boolean existsSubA(ITransaction tx,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from inheritance_test_suba where id_col = ?");
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

    private boolean existsSubB(ITransaction tx,int id) throws SQLException
    {
        boolean exists = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from inheritance_test_subb where id_col = ?");
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
        cleanupDb(dbName);
    }

    @AfterClass
    public static void after()
    {
        finalizeDb(dbName);
    }
}