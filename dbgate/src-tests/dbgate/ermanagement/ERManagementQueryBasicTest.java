package dbgate.ermanagement;

import dbgate.ColumnType;
import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.ermanagement.query.*;
import dbgate.ermanagement.query.expr.ConditionExpr;
import dbgate.ermanagement.query.expr.GroupConditionExpr;
import dbgate.ermanagement.query.expr.JoinExpr;
import dbgate.ermanagement.support.query.basic.QueryBasicDetailsEntity;
import dbgate.ermanagement.support.query.basic.QueryBasicEntity;
import dbgate.ermanagement.support.query.basic.QueryBasicJoinEntity;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class ERManagementQueryBasicTest
{
    private static DBConnector connector;
    private int[] basicEntityIds;
    private String[] basicEntityNames;
    private boolean[] hasOverrideChildren;

    private Collection<QueryBasicEntity> basicEntities;
    private Collection<QueryBasicDetailsEntity> detailsEntities;

    @BeforeClass
    public static void before()
    {
        try
        {
            Logger.getLogger(ERManagementQueryBasicTest.class.getName()).info("Starting in-memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:memory:init-testing-query-basic;create=true");

            String sql = "Create table query_basic (\n" +
                        "\tid_col Int NOT NULL,\n" +
                        "\tname Varchar(20) NOT NULL,\n" +
                        " Primary Key (id_col))";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table query_basic_join (\n" +
                    "\tid_col Int NOT NULL,\n" +
                    "\tname Varchar(20) NOT NULL,\n" +
                    "\toverride_description Varchar(50) NOT NULL,\n" +
                    " Primary Key (id_col,name))";
            ps = con.prepareStatement(sql);
            ps.execute();

            sql = "Create table query_basic_details (\n" +
                    "\tname Varchar(20) NOT NULL,\n" +
                    "\tdescription Varchar(50) NOT NULL )";
            ps = con.prepareStatement(sql);
            ps.execute();

            con.commit();
            con.close();

            connector = new DBConnector("jdbc:derby:memory:init-testing-query-basic;","org.apache.derby.jdbc.EmbeddedDriver",DBConnector.DB_DERBY);

            ERLayer.getSharedInstance().getConfig().setAutoTrackChanges(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.getLogger(ERManagementQueryBasicTest.class.getName()).severe("Exception during database startup.");
        }
    }

    @Before
    public void beforeEach()
    {
        if (DBConnector.getSharedInstance() != null)
        {
            ERLayer.getSharedInstance().clearCache();
        }
    }

    @Test
    public void queryBasic_basic_withSql_shouldSelectAll()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.rawSql("id_col"))
                    .select(QuerySelection.rawSql("name as name_col"));

            Collection results = query.toList(connection);

            Assert.assertTrue(results.size() == 4);
            for (Object result : results)
            {
                Object[] resultArray = (Object[]) result;
                int id = (Integer) resultArray[0];
                String name = (String) resultArray[1];

                QueryBasicEntity entity = getById(id);
                Assert.assertEquals(entity.getName(),name);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_distinct_withSql_shouldSelectDistinct()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.rawSql("name as name_col"))
                    .distinct();

            Collection results = query.toList(connection);

            Assert.assertTrue(results.size() == 2);
            int count = 0;
            for (Object result : results)
            {
                Object[] resultArray = (Object[]) result;
                String name = (String) resultArray[0];

                for (QueryBasicEntity basicEntity : basicEntities)
                {
                    if (basicEntity.getName().equals(name))
                        count++;
                }
            }
            Assert.assertTrue(count == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_skip_withSql_shouldSkip()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.type(QueryBasicEntity.class))
                    .skip(1);

            Collection results = query.toList(connection);
            hasIds(results, Arrays.copyOfRange(basicEntityIds,1,4));
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_fetch_withSql_shouldFetch()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.type(QueryBasicEntity.class))
                    .fetch(2);

            Collection results = query.toList(connection);
            hasIds(results, Arrays.copyOfRange(basicEntityIds,0,2));
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_skipAndFetch_withSql_shouldSkipAndFetch()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.type(QueryBasicEntity.class))
                    .skip(1).fetch(2);

            Collection results = query.toList(connection);
            hasIds(results, Arrays.copyOfRange(basicEntityIds,1,3));
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_select_withTypeSelection_shouldSelectAll()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
            for (Object result : results)
            {
                Object[] resultArray = (Object[]) result;
                QueryBasicEntity loadedEntity = (QueryBasicEntity) resultArray[0];

                QueryBasicEntity orgEntity = getById(loadedEntity.getIdCol());
                Assert.assertEquals(loadedEntity.getName(),orgEntity.getName());
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_select_withSubQuerySelection_shouldSelectAll()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery descriptionQuery = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicDetailsEntity.class,"qbd1"))
                    .where(QueryCondition.rawSql("qbd1.name = qb1.name"))
                    .select(QuerySelection.rawSql("qbd1.description")).fetch(1);

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .select(QuerySelection.type(QueryBasicEntity.class))
                    .select(QuerySelection.query(descriptionQuery,"description"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
            for (Object result : results)
            {
                Object[] resultArray = (Object[]) result;
                QueryBasicEntity entity = (QueryBasicEntity) resultArray[0];
                String description = (String) resultArray[1];

                QueryBasicDetailsEntity detailsEntity = getDescriptionForName(entity.getName());
                Assert.assertEquals(detailsEntity.getDescription(),description);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_select_withFieldSelectionWithClass_shouldSelectColumn()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class, "qb1"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "name", "name1"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
            int index = 0;
            for (Object result : results)
            {
                Object[] resultArray = (Object[]) result;
                String name = (String) resultArray[0];

                Assert.assertTrue(basicEntityNames[index++].equals(name));
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

//    @Test
//    public void queryBasic_select_withFieldSelectionWithoutClass_shouldSelectColumn()
//    {
//        try
//        {
//            Connection connection = connector.getConnection();
//            createTestData(connection);
//            connection.commit();
//            connection.close();
//
//            connection = connector.getConnection();
//
//            ISelectionQuery query = new SelectionQuery()
//                    .from(QueryFrom.type(QueryBasicEntity.class, "qb1"))
//                    .select(QuerySelection.field("name","name1"));
//
//            Collection results = query.toList(connection);
//            Assert.assertTrue(results.size() == 4);
//            int index = 0;
//            for (Object result : results)
//            {
//                Object[] resultArray = (Object[]) result;
//                String name = (String) resultArray[0];
//
//                Assert.assertTrue(basicEntityNames[index++].equals(name));
//            }
//        }
//        catch (Exception e)
//        {
//            Assert.fail(e.getMessage());
//            e.printStackTrace();
//        }
//    }

    @Test
    public void queryBasic_select_withSumSelection_shouldSelectSum()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .select(QuerySelection.sum(QueryBasicEntity.class, "idCol", "id_sum"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 1);
            int sum = 0;
            for (QueryBasicEntity entity : basicEntities)
            {
                sum += entity.getIdCol();
            }
            for (Object result : results)
            {
                Object[] resultArray = (Object[]) result;
                int resultSum = (Integer) resultArray[0];

                Assert.assertTrue(sum == resultSum);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_select_withCountSelection_shouldSelectCount()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .select(QuerySelection.count(QueryBasicEntity.class, "idCol", "id_count"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 1);
            for (Object result : results)
            {
                Object[] resultArray = (Object[]) result;
                int resultCount = (Integer) resultArray[0];

                Assert.assertTrue(resultCount == 4);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_select_withCustomFunctionCountAsExampleSelection_shouldSelectCount()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .select(QuerySelection.custFunction("Count",QueryBasicEntity.class, "idCol", "id_count"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 1);
            for (Object result : results)
            {
                Object[] resultArray = (Object[]) result;
                int resultCount = (Integer) resultArray[0];

                Assert.assertTrue(resultCount == 4);
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_from_withTypeFrom_shouldSelectAll()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results,basicEntityIds);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_from_withQueryFrom_shouldSelectAll()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery from = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.query(from,"qb1"))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results,basicEntityIds);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_from_withQueryUnionFrom_shouldSelectUnion()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery fromBasic = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .select(QuerySelection.rawSql("name as name1"));

            ISelectionQuery fromDetails = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicDetailsEntity.class))
                    .select(QuerySelection.rawSql("name as name1"));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.queryUnion(true,fromBasic,fromDetails))
                    .select(QuerySelection.rawSql("name1"));

            Collection results = query.toList(connection);

            Assert.assertTrue(results.size() == 6);
            int index = 0;
            for (Object result : results)
            {
                Object[] resultArray = (Object[]) result;
                String name = (String) resultArray[0];

                if (index < 4)
                {
                    Assert.assertTrue(basicEntityNames[index++].equals(name));
                }
                else
                {
                    QueryBasicDetailsEntity detailsEntity = (QueryBasicDetailsEntity)detailsEntities.toArray()[index++ - 4];
                    Assert.assertTrue(detailsEntity.getName().equals(name));
                }
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withSql_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .where(QueryCondition.rawSql("id_col = 35"))
                    .where(QueryCondition.rawSql("name like 'Org-NameA'"))
                    .select(QuerySelection.rawSql("id_col,name"));

            Collection results = query.toList(connection);
            hasIds(results, 35);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withEqExpression_withValue_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").eq().value(ColumnType.INTEGER,35)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 35);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withNeqExpression_withValue_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").neq().value(ColumnType.INTEGER,35)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results,45,55,65);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withGtExpression_withValue_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").gt().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 55, 65);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withGeExpression_withValue_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").ge().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 45, 55, 65);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withLtExpression_withValue_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").lt().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results,35);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withLeExpression_withValue_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").le().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 35, 45);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withLikeExpression_withValue_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "name").like().value(ColumnType.VARCHAR,"Org-NameA")))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 35, 45, 55);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withNeqExpression_withField_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicDetailsEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicDetailsEntity.class, "name").neq().field(QueryBasicDetailsEntity.class, "description")))
                    .select(QuerySelection.type(QueryBasicDetailsEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 2);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withGtExpression_withQuery_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery subQuery = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class, "qbd1"))
                    .orderBy(QueryOrderBy.rawSql("id_col"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "idCol", "id_col")).fetch(1);

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").gt().query(subQuery)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, Arrays.copyOfRange(basicEntityIds, 1, 4));
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withBetweenExpression_withValue_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").between().values(ColumnType.INTEGER,35,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 35, 45, 55);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withInExpression_withValue_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,35,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 35, 55);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withInExpression_withQuery_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery subQuery = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .select(QuerySelection.field(QueryBasicEntity.class, "idCol", null));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class,"idCol").in().query(subQuery)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results,basicEntityIds);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withExistsExpression_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery subQuery = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicDetailsEntity.class,"qbd1"))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicDetailsEntity.class,"qbd1","name").eq().field(QueryBasicEntity.class,"qb1","name")))
                    .select(QuerySelection.type(QueryBasicDetailsEntity.class));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .query(subQuery).exists()))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, basicEntityIds);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withNotExistsExpression_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();

            ISelectionQuery subQuery = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicDetailsEntity.class,"qbd1"))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicDetailsEntity.class,"qbd1","name").eq().field(QueryBasicEntity.class,"qb1","name")))
                    .select(QuerySelection.type(QueryBasicDetailsEntity.class));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .query(subQuery).notExists()))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 0);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withSimpleMergeExpressionSingleAnd_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,35,55)
                        .and().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,45,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 55);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withSimpleMergeExpressionTwoOr_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,35,55)
                        .or().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,55)
                        .or().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,45,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 35, 45, 55);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withSimpleMergeExpressionSingleAndSingleOr_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,35,55)
                        .or().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,55)
                        .and().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,45,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results,35,55);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withComplexMergeExpressionCombinedTwoAndsWithOr_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .and(ConditionExpr.build()
                                     .field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER, 35, 55)
                                , ConditionExpr.build()
                                .field(QueryBasicEntity.class, "idCol").eq().value(ColumnType.INTEGER, 55))
                        .or().field(QueryBasicEntity.class, "idCol").eq().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 45, 55);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withComplexMergeExpressionCombinedTwoOrsWithOr_shouldSelectMatching()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .or(ConditionExpr.build()
                            .field(QueryBasicEntity.class,"idCol").in().values(ColumnType.INTEGER,35,55)
                            ,ConditionExpr.build()
                            .field(QueryBasicEntity.class,"idCol").eq().value(ColumnType.INTEGER,55))
                        .or().field(QueryBasicEntity.class,"idCol").eq().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, 35, 45, 55);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_join_withBasicSql_shouldJoin()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .join(QueryJoin.rawSql("inner join query_basic_details qbd1 on qb1.name = qbd1.name"))
                    .orderBy(QueryOrderBy.rawSql("qb1.name"))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            hasIds(results, basicEntityIds);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_join_withEntityDefinedJoinDirectionOfDefinition_shouldJoin()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .join(QueryJoin.type(QueryBasicEntity.class,QueryBasicJoinEntity.class,"qbj1"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "idCol", null));

            Collection results = query.toList(connection);
            hasIds(results, 35, 65);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_join_withEntityDefinedJoinDirectionOppositeToDefinition_shouldJoin()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicJoinEntity.class,"qbj1"))
                    .join(QueryJoin.type(QueryBasicJoinEntity.class,QueryBasicEntity.class,"qb1"))
                    .select(QuerySelection.field(QueryBasicJoinEntity.class, "idCol", null));

            Collection results = query.toList(connection);
            hasIds(results, 35, 65);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_join_withEntityDefinedJoinWithOuterJoin_shouldJoin()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .join(QueryJoin.type(QueryBasicEntity.class, QueryBasicJoinEntity.class, "qbj1",
                                         QueryJoinType.LEFT))
                    .select(QuerySelection.field(QueryBasicEntity.class, "idCol", null));

            Collection results = query.toList(connection);
            hasIds(results, basicEntityIds);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_join_withoutEntityDefinedJoinWithExpression_shouldJoin()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class,"qb1"))
                    .join(QueryJoin.type(QueryBasicEntity.class
                            , QueryBasicDetailsEntity.class
                            , JoinExpr.build()
                                .field(QueryBasicEntity.class,"name").eq().field(QueryBasicDetailsEntity.class,"name")
                            , "qbd1"))
                    .select(QuerySelection.field(QueryBasicDetailsEntity.class, "description", null));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_group_withBasicSql_shouldGroup()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .groupBy(QueryGroup.rawSql("name"))
                    .select(QuerySelection.rawSql("name"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 2);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_group_withExpression_shouldGroup()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class, "qb"))
                    .groupBy(QueryGroup.field(QueryBasicEntity.class, "name"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "name", null));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 2);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_groupCondition_withBasicSql_shouldSelectMatchingGroups()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .groupBy(QueryGroup.rawSql("name"))
                    .having(QueryGroupCondition.rawSql("count(id_col)>1"))
                    .select(QuerySelection.rawSql("name"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 1);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_groupCondition_withExpressionCount_shouldSelectMatchingGroups()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class, "qb"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "name", null))
                    .groupBy(QueryGroup.field(QueryBasicEntity.class, "name"))
                    .having(QueryGroupCondition.expression(
                            GroupConditionExpr.build()
                                .field(QueryBasicEntity.class, "name").count().gt().value(ColumnType.INTEGER,1)
                    ));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 1);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_orderBy_withBasicSql_shouldSelectOrdered()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .orderBy(QueryOrderBy.rawSql("name"))
                    .select(QuerySelection.rawSql("name"));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_orderBy_withExpression_shouldSelectOrdered()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .orderBy(QueryOrderBy.field(QueryBasicEntity.class, "name"))
                    .orderBy(QueryOrderBy.field(QueryBasicEntity.class,"idCol"))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_orderBy_withExpressionDesc_shouldSelectOrdered()
    {
        try
        {
            Connection connection = connector.getConnection();
            createTestData(connection);
            connection.commit();
            connection.close();

            connection = connector.getConnection();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .orderBy(QueryOrderBy.field(QueryBasicEntity.class, "name", QueryOrderType.DESCEND))
                    .orderBy(QueryOrderBy.field(QueryBasicEntity.class, "idCol", QueryOrderType.DESCEND))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean hasIds(Collection list,int... ids)
    {
        if (list.size() != ids.length)
            return false;

        for (int id : ids)
        {
            boolean found = false;
            for (Object listItem : list)
            {
                if (listItem instanceof QueryBasicEntity)
                {
                    found = found || ((QueryBasicEntity)listItem).getIdCol() == id;
                }
                else if (listItem instanceof Object[])
                {
                    Object[] items = (Object[])listItem;
                    for (Object item : items)
                    {
                        if (item instanceof Integer)
                        {
                            found = found || ((Integer)item) == id;
                        }
                    }
                }
                else
                {
                    found = found || ((Integer)listItem) == id;
                }
            }
            if (!found)
            {
                return false;
            }
        }

        return true;
    }

    private QueryBasicEntity getById(int id)
    {
        for (QueryBasicEntity basicEntity : basicEntities)
        {
            if (basicEntity.getIdCol() == id)
                return basicEntity;
        }
        return null;
    }

    private QueryBasicDetailsEntity getDescriptionForName(String name)
    {
        for (QueryBasicDetailsEntity detailsEntity : detailsEntities)
        {
            if (detailsEntity.getName().equals(name))
                return detailsEntity;
        }
        return null;
    }

    private void createTestData(Connection connection) throws PersistException
    {
        basicEntityIds = new int[]{35,45,55,65};
        basicEntityNames = new String[]{"Org-NameA","Org-NameA","Org-NameA","Org-NameB"};
        hasOverrideChildren = new boolean[]{true,false,false,true};

        basicEntities = new ArrayList<>();
        detailsEntities = new ArrayList<>();

        for (int i = 0, basicEntityIdsLength = basicEntityIds.length; i < basicEntityIdsLength; i++)
        {
            int basicEntityId = basicEntityIds[i];
            QueryBasicEntity entity = new QueryBasicEntity();
            entity.setIdCol(basicEntityId);
            entity.setName(basicEntityNames[i]);
            if (hasOverrideChildren[i])
            {
                QueryBasicJoinEntity joinEntity = new QueryBasicJoinEntity();
                joinEntity.setOverrideDescription(entity.getName() + "Details");
                entity.setJoinEntity(joinEntity);
            }
            entity.persist(connection);
            basicEntities.add(entity);
        }

        for (String basicEntityName : basicEntityNames)
        {
            boolean found = false;
            for (QueryBasicDetailsEntity detailsEntity : detailsEntities)
            {
                if (detailsEntity.getName().equals(basicEntityName))
                {
                    found = true;
                    break;
                }
            }
            if (found)
            {
                continue;
            }

            QueryBasicDetailsEntity detailsEntity = new QueryBasicDetailsEntity();
            detailsEntity.setName(basicEntityName);
            detailsEntity.setDescription(basicEntityName + "Details");
            detailsEntity.persist(connection);
            detailsEntities.add(detailsEntity);
        }
    }

    @After
    public void afterEach()
    {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:derby:memory:init-testing-query-basic;create=true");

            PreparedStatement ps = con.prepareStatement("DELETE FROM query_basic");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM query_basic_join");
            ps.execute();

            ps = con.prepareStatement("DELETE FROM query_basic_details");
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
        Logger.getLogger(ERManagementQueryBasicTest.class.getName()).info("Stopping in-memory database.");
        try
        {
            DriverManager.getConnection("jdbc:derby:memory:init-testing-query-basic;shutdown=true").close();
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
            VFMemoryStorageFactory.purgeDatabase(new File("init-testing-query-basic").getCanonicalPath());
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
    }
}