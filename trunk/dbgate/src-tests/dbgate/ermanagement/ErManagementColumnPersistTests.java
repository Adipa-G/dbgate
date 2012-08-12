package dbgate.ermanagement;

import dbgate.EntityStatus;
import dbgate.DateWrapper;
import dbgate.TimeStampWrapper;
import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.impl.DbGate;
import dbgate.ermanagement.support.persistant.columntest.*;
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
public class ErManagementColumnPersistTests
{
    private static DBConnector connector;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(ErManagementColumnPersistTests.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-column-persist;create=true");

            String sql = "Create table column_test_entity (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tlong_not_null Bigint NOT NULL,\n" +
                        "\tlong_null Bigint,\n" +
                        "\tboolean_not_null SmallInt NOT NULL,\n" +
                        "\tboolean_null SmallInt,\n" +
                        "\tchar_not_null Char(1) NOT NULL,\n" +
                        "\tchar_null Char(1),\n" +
                        "\tint_not_null Int NOT NULL,\n" +
                        "\tint_null Int,\n" +
                        "\tdate_not_null Date NOT NULL,\n" +
                        "\tdate_null Date,\n" +
                        "\tdouble_not_null Double NOT NULL,\n" +
                        "\tdouble_null Double,\n" +
                        "\tfloat_not_null Float NOT NULL,\n" +
                        "\tfloat_null Float,\n" +
                        "\ttimestamp_not_null Timestamp NOT NULL,\n" +
                        "\ttimestamp_null Timestamp,\n" +
                        "\tvarchar_not_null Varchar(20) NOT NULL,\n" +
                        "\tvarchar_null Varchar(20),\n" +
                        " Primary Key (id_col))";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DBConnector("jdbc:derby:memory:unit-testing-column-persist;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);

            DbGate.getSharedInstance().getConfig().setAutoTrackChanges(false);
            DbGate.getSharedInstance().getConfig().setCheckVersion(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(ErManagementColumnPersistTests.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        if (DBConnector.getSharedInstance() != null)
        {
            DbGate.getSharedInstance().clearCache();
        }
    }

    @Test
    public void columnPersist_insert_withFieldsDifferentTypesWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity entity = new ColumnTestEntityFields();
            createEntityWithNonNullValues(entity);

            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(entity,loadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_insert_withExtsDifferentTypesWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            Class type = ColumnTestEntityExts.class;
            DbGate.getSharedInstance().registerEntity(type,ColumnTestExtFactory.getTableNames(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity entity = new ColumnTestEntityExts();
            createEntityWithNonNullValues(entity);

            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(entity,loadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_insert_withAnnotationsDifferentTypesWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity entity = new ColumnTestEntityAnnotations();
            createEntityWithNonNullValues(entity);

            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(entity,loadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_insert_withFieldsDifferentTypesWithNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity entity = new ColumnTestEntityFields();
            createEntityWithNullValues(entity);

            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(entity,loadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

     @Test
    public void columnPersist_insert_withExtsDifferentTypesWithNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            Class type = ColumnTestEntityExts.class;
            DbGate.getSharedInstance().registerEntity(type,ColumnTestExtFactory.getTableNames(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity entity = new ColumnTestEntityExts();
            createEntityWithNullValues(entity);

            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(entity,loadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_insert_withAnnotationsDifferentTypesWithNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity entity = new ColumnTestEntityAnnotations();
            createEntityWithNullValues(entity);

            entity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(entity,loadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withFieldsDifferentTypesStartWithoutNullEndWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id =(Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withExtsDifferentTypesStartWithoutNullEndWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            Class type = ColumnTestEntityExts.class;
            DbGate.getSharedInstance().registerEntity(type,ColumnTestExtFactory.getTableNames(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withAnnotationsDifferentTypesStartWithoutNullEndWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withFieldsDifferentTypesStartWithoutNullEndWithNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withExtsDifferentTypesStartWithoutNullEndWithNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            Class type = ColumnTestEntityExts.class;
            DbGate.getSharedInstance().registerEntity(type,ColumnTestExtFactory.getTableNames(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withAnnotationsDifferentTypesStartWithoutNullEndWithNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withFieldsDifferentTypesStartWithNullEndWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withExtsDifferentTypesStartWithNullEndWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            Class type = ColumnTestEntityExts.class;
            DbGate.getSharedInstance().registerEntity(type,ColumnTestExtFactory.getTableNames(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withAnnotationsDifferentTypesStartWithNullEndWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withFieldsDifferentTypesStartWithNullEndWithNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id =(Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withExtsDifferentTypesStartWithNullEndWithNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            Class type = ColumnTestEntityExts.class;
            DbGate.getSharedInstance().registerEntity(type,ColumnTestExtFactory.getTableNames(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_update_withAnnotationsDifferentTypesStartWithNullEndWithNull_shouldEqualWhenLoaded()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            connection = connector.getConnection();
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            assertTwoEntitiesEquals(loadedEntity,reLoadedEntity);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_delete_withFieldsDifferentTypesStartWithNullEndWithNull_shouldDelete()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            connection = connector.getConnection();
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            boolean  loaded = loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            Assert.assertFalse(loaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_delete_withExtsDifferentTypesStartWithNullEndWithNull_shouldDelete()
    {
        try
        {
            Connection connection = connector.getConnection();

            Class type = ColumnTestEntityExts.class;
            DbGate.getSharedInstance().registerEntity(type,ColumnTestExtFactory.getTableNames(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            connection = connector.getConnection();
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            boolean  loaded = loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            Assert.assertFalse(loaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void columnPersist_delete_withAnnotationsDifferentTypesStartWithNullEndWithNull_shouldDelete()
    {
        try
        {
            Connection connection = connector.getConnection();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(connection);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNullValues(newEntity);
            newEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(connection,loadedEntity,id);
            connection.close();

            connection = connector.getConnection();
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            boolean loaded = loadEntityWithId(connection,reLoadedEntity,id);
            connection.close();

            Assert.assertFalse(loaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadEntityWithId(Connection connection, IColumnTestEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = connection.prepareStatement("select * from column_test_entity where id_col = ?");
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

    private void createEntityWithNonNullValues(IColumnTestEntity entity)
    {
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
    }

    private void createEntityWithNullValues(IColumnTestEntity entity)
    {
        entity.setBooleanNotNull(true);
        entity.setBooleanNull(null);
        entity.setCharNotNull('A');
        entity.setCharNull(null);
        entity.setDateNotNull(new DateWrapper());
        entity.setDateNull(null);
        entity.setDoubleNotNull(5D);
        entity.setDoubleNull(null);
        entity.setFloatNotNull(20F);
        entity.setFloatNull(null);
        entity.setIntNotNull(24);
        entity.setIntNull(null);
        entity.setLongNotNull(356L);
        entity.setLongNull(null);
        entity.setTimestampNotNull(new TimeStampWrapper());
        entity.setTimestampNull(null);
        entity.setVarcharNotNull("notNull");
        entity.setVarcharNull(null);
    }

    private void updateEntityWithNonNullValues(IColumnTestEntity entity)
    {
        entity.setBooleanNotNull(false);
        entity.setBooleanNull(false);
        entity.setCharNotNull('C');
        entity.setCharNull('D');
        entity.setDateNotNull(new DateWrapper(2010,10,15));
        entity.setDateNull(new DateWrapper(2010,10,11));
        entity.setDoubleNotNull(53D);
        entity.setDoubleNull(65D);
        entity.setFloatNotNull(20465F);
        entity.setFloatNull(32420F);
        entity.setIntNotNull(35424);
        entity.setIntNull(46723);
        entity.setLongNotNull(3565535L);
        entity.setLongNull(2245326L);
        entity.setTimestampNotNull(new TimeStampWrapper(new DateWrapper(2010,4,5)));
        entity.setTimestampNull(new TimeStampWrapper(new DateWrapper(2010,6,5)));
        entity.setVarcharNotNull("notNull string");
        entity.setVarcharNull("null string");
    }

    private void updateEntityWithNullValues(IColumnTestEntity entity)
    {
        entity.setBooleanNotNull(false);
        entity.setBooleanNull(null);
        entity.setCharNotNull('C');
        entity.setCharNull(null);
        entity.setDateNotNull(new DateWrapper(2010,10,15));
        entity.setDateNull(null);
        entity.setDoubleNotNull(53D);
        entity.setDoubleNull(null);
        entity.setFloatNotNull(20465F);
        entity.setFloatNull(null);
        entity.setIntNotNull(35424);
        entity.setIntNull(null);
        entity.setLongNotNull(3565535L);
        entity.setLongNull(null);
        entity.setTimestampNotNull(new TimeStampWrapper(new DateWrapper(2010,4,5)));
        entity.setTimestampNull(null);
        entity.setVarcharNotNull("notNull string");
        entity.setVarcharNull(null);
    }

    private void assertTwoEntitiesEquals(IColumnTestEntity entityA, IColumnTestEntity entityB)
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
    }

    @After
    public void afterEach()
    {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:derby:memory:unit-testing-column-persist;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM COLUMN_TEST_ENTITY");
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
        Logger.getLogger(ErManagementColumnPersistTests.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-column-persist;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("unit-testing-column-persist").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}