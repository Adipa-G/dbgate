package dbgate;

import dbgate.caches.impl.EntityInfo;
import dbgate.caches.impl.EntityInfoCache;
import dbgate.ermanagement.ermapper.DbGateConfig;
import dbgate.ermanagement.ermapper.utils.ReflectionUtils;
import dbgate.exceptions.common.*;
import dbgate.support.exception.EntityWithNoDefaultConstructor;
import org.junit.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateExceptionTest extends AbstractDbGateTestBase
{
    private static final String dbName = "unit-testing-exceptions";

    @BeforeClass
    public static void before()
    {
        testClass = DbGateExceptionTest.class;
        beginInit(dbName);

        String sql = "Create table exception_test_root (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        sql = "Create table exception_test_one2many (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tindex_no Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col,index_no))";
        createTableFromSql(sql,dbName);

        sql = "Create table exception_test_one2one (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        endInit(dbName);
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
        connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
        connector.getDbGate().getConfig().setDefaultVerifyOnWriteStrategy(VerifyOnWriteStrategy.DO_NOT_VERIFY);
    }

    @Test
    public void EntityInstantiationException_EntityWithoutDefaultConstructor_ShouldFail()
    {
        try
        {
            ReflectionUtils.createInstance(EntityWithNoDefaultConstructor.class);
            Assert.fail("could create instance without default constructor");
        }
        catch (EntityInstantiationException ex)
        {
            Assert.assertTrue("could not create instance without default constructor",true);
        }
    }

    @Test
    public void EntityRegistrationException_EntityWithoutDefaultConstructor_ShouldFail()
    {
        try
        {
            EntityInfoCache cache = new EntityInfoCache(new DbGateConfig());
            cache.register(EntityWithNoDefaultConstructor.class);
            Assert.fail("could register class without default constructor");
        }
        catch (EntityRegistrationException e)
        {
            Assert.assertTrue("could not register class without default constructor",true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail("unexpected exception"  + e.getMessage());
        }
    }

    @Test
    public void MethodInvocationException_EntityWithExceptionsWhenGetterInvoked_ShouldFail()
    {
        try
        {
            EntityWithNoDefaultConstructor entity = new EntityWithNoDefaultConstructor("");
            Method getter = entity.getClass().getMethod("getIdCol");
            ReflectionUtils.getValue(getter, entity);
            Assert.fail("could invoke getter method");
        }
        catch (MethodInvocationException e)
        {
            Assert.assertTrue("could not invoke getter method",true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail("unexpected exception"  + e.getMessage());
        }
    }

    @Test
    public void MethodInvocationException_EntityWithExceptionsWhenSetterInvoked_ShouldFail()
    {
        try
        {
            EntityWithNoDefaultConstructor entity = new EntityWithNoDefaultConstructor("");
            Method setter = entity.getClass().getMethod("setIdCol", Integer.TYPE);
            ReflectionUtils.setValue(setter, entity, 0);
            Assert.fail("could invoke setter method");
        }
        catch (MethodInvocationException e)
        {
            Assert.assertTrue("could not invoke setter method",true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail("unexpected exception"  + e.getMessage());
        }
    }

    @Test
    public void MethodNotFoundException_GetInfoOnNonExistentMethod_ShouldFail()
    {
        try
        {
            EntityInfo info = new EntityInfo(EntityWithNoDefaultConstructor.class);
            info.getGetter("nonExistent");
            Assert.fail("could get method");
        }
        catch (MethodNotFoundException e)
        {
            Assert.assertTrue("could not get method",true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail("unexpected exception"  + e.getMessage());
        }
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