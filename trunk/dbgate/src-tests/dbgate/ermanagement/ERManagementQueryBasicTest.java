package dbgate.ermanagement;

import dbgate.DBColumnType;
import dbgate.dbutility.DBConnector;
import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.ermanagement.query.*;
import dbgate.ermanagement.query.expr.ConditionExpr;
import dbgate.ermanagement.query.expr.JoinExpr;
import dbgate.ermanagement.support.query.basic.QueryBasicDetailsEntity;
import dbgate.ermanagement.support.query.basic.QueryBasicEntity;
import dbgate.ermanagement.support.query.basic.QueryBasicJoinEntity;
import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class ERManagementQueryBasicTest
{
    private static DBConnector connector;
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
    public void basic_withSql_shouldSelectAll()
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
    public void distinct_withSql_shouldSelectDistinct()
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
    public void skip_withSql_shouldSkip()
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
                    .skip(1);

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 3);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void fetch_withSql_shouldFetch()
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
                    .fetch(2);

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
    public void skipAndFetch_withSql_shouldSkipAndFetch()
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
                    .skip(1).fetch(2);

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
    public void select_withTypeSelection_shouldSelectAll()
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
    public void select_withSubQuerySelection_shouldSelectAll()
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
    public void select_withColumnSelection_shouldSelectAll()
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
                    .select(QuerySelection.column(QueryBasicEntity.class,"name","name1"));

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
    public void select_withSumSelection_shouldSelectSum()
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
    public void select_withCountSelection_shouldSelectCount()
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
    public void select_withCustomFunctionCountAsExampleSelection_shouldSelectCount()
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
    public void from_withTypeFrom_shouldSelectAll()
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
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void from_withQueryFrom_shouldSelectAll()
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
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void from_withQueryUnionFrom_shouldSelectUnion()
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
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void condition_withSql_shouldSelectMatching()
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
            Assert.assertTrue(results.size() == 1);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void condition_withEqExpression_withValue_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").eq().value(DBColumnType.INTEGER,35)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

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
    public void condition_withNeqExpression_withValue_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").neq().value(DBColumnType.INTEGER,35)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 3);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void condition_withGtExpression_withValue_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").gt().value(DBColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

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
    public void condition_withGeExpression_withValue_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").ge().value(DBColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 3);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void condition_withLtExpression_withValue_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").lt().value(DBColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

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
    public void condition_withLeExpression_withValue_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").le().value(DBColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

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
    public void condition_withLikeExpression_withValue_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "name").like().value(DBColumnType.VARCHAR,"Org-NameA")))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 3);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void condition_withNeqExpression_withField_shouldSelectMatching()
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
    public void condition_withGtExpression_withQuery_shouldSelectMatching()
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
                    .select(QuerySelection.column(QueryBasicEntity.class, "idCol", "id_col")).fetch(1);

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").gt().query(subQuery)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 3);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void condition_withBetweenExpression_withValue_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").between().values(DBColumnType.INTEGER,35,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 3);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void condition_withInExpression_withValue_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER,35,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

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
    public void condition_withInExpression_withQuery_shouldSelectMatching()
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
                    .select(QuerySelection.column(QueryBasicEntity.class,"idCol",null));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.type(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class,"idCol").in().query(subQuery)))
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
    public void condition_withExistsExpression_shouldSelectMatching()
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
            Assert.assertTrue(results.size() == 4);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void condition_withNotExistsExpression_shouldSelectMatching()
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
    public void condition_withSimpleMergeExpression_SingleAnd_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER,35,55)
                        .and().field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER,45,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

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
    public void condition_withSimpleMergeExpression_TwoOr_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER,35,55)
                        .or().field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER,55)
                        .or().field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER,45,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 3);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void condition_withSimpleMergeExpression_SingleAndSingleOr_shouldSelectMatching()
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
                        .field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER,35,55)
                        .or().field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER,55)
                        .and().field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER,45,55)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

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
    public void condition_withComplexMergeExpression_CombinedTwoAndsWithOr_shouldSelectMatching()
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
                                     .field(QueryBasicEntity.class, "idCol").in().values(DBColumnType.INTEGER, 35, 55)
                                , ConditionExpr.build()
                                .field(QueryBasicEntity.class, "idCol").eq().value(DBColumnType.INTEGER, 55))
                        .or().field(QueryBasicEntity.class, "idCol").eq().value(DBColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

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
    public void condition_withComplexMergeExpression_CombinedTwoOrsWithOr_shouldSelectMatching()
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
                            .field(QueryBasicEntity.class,"idCol").in().values(DBColumnType.INTEGER,35,55)
                            ,ConditionExpr.build()
                            .field(QueryBasicEntity.class,"idCol").eq().value(DBColumnType.INTEGER,55))
                        .or().field(QueryBasicEntity.class,"idCol").eq().value(DBColumnType.INTEGER,45)))
                    .select(QuerySelection.type(QueryBasicEntity.class));

            Collection results = query.toList(connection);
            Assert.assertTrue(results.size() == 3);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void join_withBasicSql_shouldJoin()
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
                    .select(QuerySelection.rawSql("qb1.name as name"))
                    .select(QuerySelection.rawSql("description"));

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
    public void join_withEntityDefinedJoin_directionOfDefinition_shouldJoin()
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
                    .select(QuerySelection.column(QueryBasicEntity.class, "idCol", null));

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
    public void join_withEntityDefinedJoin_directionOppositeToDefinition_shouldJoin()
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
                    .select(QuerySelection.column(QueryBasicJoinEntity.class, "idCol", null));

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
    public void join_withEntityDefinedJoin_withOuterJoin_shouldJoin()
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
                    .select(QuerySelection.column(QueryBasicEntity.class, "idCol", null));

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
    public void join_withoutEntityDefinedJoin_withExpression_shouldJoin()
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
                    .select(QuerySelection.column(QueryBasicDetailsEntity.class, "description", null));

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
    public void group_withBasicSql_shouldGroup()
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
    public void groupCondition_withBasicSql_shouldSelectMatchingGroups()
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
    public void orderBy_withBasicSql_shouldSelectOrdered()
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
        basicEntities = new ArrayList<>();
        detailsEntities = new ArrayList<>();

        int id = 35;
        QueryBasicEntity entity = new QueryBasicEntity();
        entity.setIdCol(id);
        entity.setName("Org-NameA");
        QueryBasicJoinEntity joinEntity = new QueryBasicJoinEntity();
        joinEntity.setOverrideDescription(entity.getName() + "Details");
        entity.setJoinEntity(joinEntity);
        entity.persist(connection);
        basicEntities.add(entity);

        QueryBasicDetailsEntity detailsEntity = new QueryBasicDetailsEntity();
        detailsEntity.setName(entity.getName());
        detailsEntity.setDescription(entity.getName() + "Details");
        detailsEntity.persist(connection);
        detailsEntities.add(detailsEntity);

        id = 45;
        entity = new QueryBasicEntity();
        entity.setIdCol(id);
        entity.setName("Org-NameA");
        entity.persist(connection);
        basicEntities.add(entity);

        id = 55;
        entity = new QueryBasicEntity();
        entity.setIdCol(id);
        entity.setName("Org-NameA");
        entity.persist(connection);
        basicEntities.add(entity);

        id = 65;
        entity = new QueryBasicEntity();
        entity.setIdCol(id);
        entity.setName("Org-NameB");
        joinEntity = new QueryBasicJoinEntity();
        joinEntity.setOverrideDescription(entity.getName() + "Details");
        entity.setJoinEntity(joinEntity);
        entity.persist(connection);
        basicEntities.add(entity);

        detailsEntity = new QueryBasicDetailsEntity();
        detailsEntity.setName(entity.getName());
        detailsEntity.setDescription(entity.getName() + "Details");
        detailsEntity.persist(connection);
        detailsEntities.add(detailsEntity);
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
