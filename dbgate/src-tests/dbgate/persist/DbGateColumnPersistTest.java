package dbgate.persist;

import dbgate.*;
import dbgate.persist.support.columntest.*;
import junit.framework.Assert;
import org.junit.*;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Aug 29, 2010
 * Time: 6:40:58 PM
 */
public class DbGateColumnPersistTest extends AbstractDbGateTestBase
{
    private static final String dbName = "unit-testing-column-persist";

    @BeforeClass
    public static void before()
    {
        testClass = DbGateColumnPersistTest.class;
        beginInit(dbName);

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
        createTableFromSql(sql,dbName);
        endInit(dbName);
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();

        connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.MANUAL);
        connector.getDbGate().getConfig().setDefaultVerifyOnWriteStrategy(VerifyOnWriteStrategy.DO_NOT_VERIFY);
    }

    @Test
    public void columnPersist_insert_withFieldsDifferentTypesWithoutNull_shouldEqualWhenLoaded()
    {
        try
        {
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity entity = new ColumnTestEntityFields();
            createEntityWithNonNullValues(entity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx,loadedEntity,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            Class type = ColumnTestEntityExts.class;
            connector.getDbGate().registerEntity(type,ColumnTestExtFactory.getTableInfo(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity entity = new ColumnTestEntityExts();
            createEntityWithNonNullValues(entity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx,loadedEntity,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity entity = new ColumnTestEntityAnnotations();
            createEntityWithNonNullValues(entity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx,loadedEntity,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity entity = new ColumnTestEntityFields();
            createEntityWithNullValues(entity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx,loadedEntity,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            Class type = ColumnTestEntityExts.class;
            connector.getDbGate().registerEntity(type,ColumnTestExtFactory.getTableInfo(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity entity = new ColumnTestEntityExts();
            createEntityWithNullValues(entity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx,loadedEntity,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity entity = new ColumnTestEntityAnnotations();
            createEntityWithNullValues(entity);

            entity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx,loadedEntity,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id =(Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            Class type = ColumnTestEntityExts.class;
            connector.getDbGate().registerEntity(type,ColumnTestExtFactory.getTableInfo(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            Class type = ColumnTestEntityExts.class;
            connector.getDbGate().registerEntity(type,ColumnTestExtFactory.getTableInfo(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNonNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            Class type = ColumnTestEntityExts.class;
            connector.getDbGate().registerEntity(type,ColumnTestExtFactory.getTableInfo(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNonNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id =(Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            Class type = ColumnTestEntityExts.class;
            connector.getDbGate().registerEntity(type,ColumnTestExtFactory.getTableInfo(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            updateEntityWithNullValues(loadedEntity);
            loadedEntity.setStatus(EntityStatus.MODIFIED);

            tx = connector.createTransaction();
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx, reLoadedEntity, id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityFields();
            createEntityWithNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityFields();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            tx = connector.createTransaction();
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityFields();
            boolean  loaded = loadEntityWithId(tx,reLoadedEntity,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            Class type = ColumnTestEntityExts.class;
            connector.getDbGate().registerEntity(type,ColumnTestExtFactory.getTableInfo(type)
                    ,ColumnTestExtFactory.getFieldInfo(type));

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityExts();
            createEntityWithNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityExts();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            tx = connector.createTransaction();
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityExts();
            boolean  loaded = loadEntityWithId(tx,reLoadedEntity,id);
            tx.close();

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
            ITransaction tx = connector.createTransaction();

            int id = (Integer)new PrimaryKeyGenerator().getNextSequenceValue(tx);
            IColumnTestEntity newEntity = new ColumnTestEntityAnnotations();
            createEntityWithNullValues(newEntity);
            newEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity loadedEntity = new ColumnTestEntityAnnotations();
            loadEntityWithId(tx,loadedEntity, id);
            tx.close();

            tx = connector.createTransaction();
            loadedEntity.setStatus(EntityStatus.DELETED);
            loadedEntity.persist(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            IColumnTestEntity reLoadedEntity = new ColumnTestEntityAnnotations();
            boolean loaded = loadEntityWithId(tx,reLoadedEntity,id);
            tx.close();

            Assert.assertFalse(loaded);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean loadEntityWithId(ITransaction tx, IColumnTestEntity loadEntity,int id) throws Exception
    {
        boolean loaded = false;

        PreparedStatement ps = tx.getConnection().prepareStatement("select * from column_test_entity where id_col = ?");
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
        cleanupDb(dbName);
    }

    @AfterClass
    public static void after()
    {
        finalizeDb(dbName);
    }
}