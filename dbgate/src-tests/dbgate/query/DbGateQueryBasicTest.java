package dbgate.query;

import dbgate.*;
import dbgate.ermanagement.query.SelectionQuery;
import dbgate.ermanagement.query.expr.ConditionExpr;
import dbgate.ermanagement.query.expr.GroupConditionExpr;
import dbgate.ermanagement.query.expr.JoinExpr;
import dbgate.exceptions.PersistException;
import dbgate.query.support.basictest.QueryBasicDetailsEntity;
import dbgate.query.support.basictest.QueryBasicEntity;
import dbgate.query.support.basictest.QueryBasicJoinEntity;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Date: Mar 24, 2011
 * Time: 11:23:36 PM
 */
public class DbGateQueryBasicTest extends AbstractDbGateTestBase
{
    private static final String dbName = "init-testing-query-basic";

    private int[] basicEntityIds;
    private String[] basicEntityNames;
    private boolean[] hasOverrideChildren;

    private Collection<QueryBasicEntity> basicEntities;
    private Collection<QueryBasicDetailsEntity> detailsEntities;

    @BeforeClass
    public static void before()
    {
        testClass = DbGateQueryBasicTest.class;
        beginInit(dbName);

        String sql = "Create table query_basic (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                " Primary Key (id_col))";
        createTableFromSql(sql,dbName);

        sql = "Create table query_basic_join (\n" +
                "\tid_col Int NOT NULL,\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                "\toverride_description Varchar(50) NOT NULL,\n" +
                " Primary Key (id_col,name))";
        createTableFromSql(sql,dbName);

        sql = "Create table query_basic_details (\n" +
                "\tname Varchar(20) NOT NULL,\n" +
                "\tdescription Varchar(50) NOT NULL )";
        createTableFromSql(sql,dbName);

        endInit(dbName);
    }

    @Before
    public void beforeEach()
    {
        connector.getDbGate().clearCache();
        connector.getDbGate().getConfig().setDefaultDirtyCheckStrategy(DirtyCheckStrategy.AUTOMATIC);
    }

    @Test
    public void queryBasic_basic_withSql_shouldSelectAll()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.rawSql("id_col"))
                    .select(QuerySelection.rawSql("name as name_col"));

            Collection results = query.toList(tx);

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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.rawSql("name as name_col"))
                    .distinct();

            Collection results = query.toList(tx);

            Assert.assertTrue(results.size() == 2);
            int count = 0;
            for (Object result : results)
            {
                String name = (String) result;

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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.entityType(QueryBasicEntity.class))
                    .skip(1);

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.entityType(QueryBasicEntity.class))
                    .fetch(2);

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.entityType(QueryBasicEntity.class))
                    .skip(1).fetch(2);

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
            Assert.assertTrue(results.size() == 4);
            for (Object result : results)
            {
                QueryBasicEntity loadedEntity = (QueryBasicEntity) result;

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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery descriptionQuery = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicDetailsEntity.class, "qbd1"))
                    .where(QueryCondition.rawSql("qbd1.name = qb1.name"))
                    .select(QuerySelection.rawSql("qbd1.description")).fetch(1);

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .select(QuerySelection.entityType(QueryBasicEntity.class))
                    .select(QuerySelection.query(descriptionQuery,"description"));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "name", "name1"));

            Collection results = query.toList(tx);
            Assert.assertTrue(results.size() == 4);
            int index = 0;
            for (Object result : results)
            {
                String name = (String) result;

                Assert.assertTrue(basicEntityNames[index++].equals(name));
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_select_withFieldSelectionWithoutClass_shouldSelectColumn()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .select(QuerySelection.field("name","name1"));

            Collection results = query.toList(tx);
            Assert.assertTrue(results.size() == 4);
            int index = 0;
            for (Object result : results)
            {
                String name = (String) result;

                Assert.assertTrue(basicEntityNames[index++].equals(name));
            }
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_select_withSumSelection_shouldSelectSum()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .select(QuerySelection.sum(QueryBasicEntity.class, "idCol", "id_sum"));

            Collection results = query.toList(tx);
            Assert.assertTrue(results.size() == 1);
            int sum = 0;
            for (QueryBasicEntity entity : basicEntities)
            {
                sum += entity.getIdCol();
            }
            for (Object result : results)
            {
                int resultSum = (Integer) result;
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .select(QuerySelection.count(QueryBasicEntity.class, "idCol", "id_count"));

            Collection results = query.toList(tx);
            Assert.assertTrue(results.size() == 1);
            for (Object result : results)
            {
                int resultCount = (Integer) result;
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .select(QuerySelection.custFunction("Count",QueryBasicEntity.class, "idCol", "id_count"));

            Collection results = query.toList(tx);
            Assert.assertTrue(results.size() == 1);
            for (Object result : results)
            {
                int resultCount = (Integer) result;
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery from = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.query(from,"qb1"))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery fromBasic = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .select(QuerySelection.rawSql("name as name1"));

            ISelectionQuery fromDetails = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicDetailsEntity.class))
                    .select(QuerySelection.rawSql("name as name1"));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.queryUnion(true,fromBasic,fromDetails))
                    .select(QuerySelection.rawSql("name1"));

            Collection results = query.toList(tx);

            Assert.assertTrue(results.size() == 6);
            int index = 0;
            for (Object result : results)
            {
                String name = (String)result;

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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .where(QueryCondition.rawSql("id_col = 35"))
                    .where(QueryCondition.rawSql("name like 'Org-NameA'"))
                    .select(QuerySelection.rawSql("id_col,name"));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").eq().value(ColumnType.INTEGER,35)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
            hasIds(results, 35);
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void queryBasic_condition_withEqExpression_withValueWithoutType_shouldSelectMatching()
    {
        try
        {
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").eq().value(35)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").neq().value(ColumnType.INTEGER,35)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").gt().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").ge().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").lt().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").le().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "name").like().value(ColumnType.VARCHAR,"Org-NameA")))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicDetailsEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicDetailsEntity.class, "name").neq().field(QueryBasicDetailsEntity.class, "description")))
                    .select(QuerySelection.entityType(QueryBasicDetailsEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery subQuery = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qbd1"))
                    .orderBy(QueryOrderBy.rawSql("id_col"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "idCol", "id_col")).fetch(1);

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").gt().query(subQuery)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").between().values(ColumnType.INTEGER,35,55)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").in().values(35,55)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery subQuery = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .select(QuerySelection.field(QueryBasicEntity.class, "idCol", null));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class,"idCol").in().query(subQuery)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery subQuery = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicDetailsEntity.class, "qbd1"))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicDetailsEntity.class,"qbd1","name").eq().field(QueryBasicEntity.class,"qb1","name")))
                    .select(QuerySelection.entityType(QueryBasicDetailsEntity.class));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .query(subQuery).exists()))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();

            ISelectionQuery subQuery = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicDetailsEntity.class, "qbd1"))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicDetailsEntity.class,"qbd1","name").eq().field(QueryBasicEntity.class,"qb1","name")))
                    .select(QuerySelection.entityType(QueryBasicDetailsEntity.class));

            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .query(subQuery).notExists()))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,35,55)
                        .and().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,45,55)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,35,55)
                        .or().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,55)
                        .or().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,45,55)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,35,55)
                        .or().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,55)
                        .and().field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER,45,55)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .and(ConditionExpr.build()
                                     .field(QueryBasicEntity.class, "idCol").in().values(ColumnType.INTEGER, 35, 55)
                                , ConditionExpr.build()
                                .field(QueryBasicEntity.class, "idCol").eq().value(ColumnType.INTEGER, 55))
                        .or().field(QueryBasicEntity.class, "idCol").eq().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class))
                    .where(QueryCondition.expression(ConditionExpr.build()
                        .or(ConditionExpr.build()
                            .field(QueryBasicEntity.class,"idCol").in().values(ColumnType.INTEGER,35,55)
                            ,ConditionExpr.build()
                            .field(QueryBasicEntity.class,"idCol").eq().value(ColumnType.INTEGER,55))
                        .or().field(QueryBasicEntity.class,"idCol").eq().value(ColumnType.INTEGER,45)))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .join(QueryJoin.rawSql("inner join query_basic_details qbd1 on qb1.name = qbd1.name"))
                    .orderBy(QueryOrderBy.rawSql("qb1.name"))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .join(QueryJoin.entityType(QueryBasicEntity.class, QueryBasicJoinEntity.class, "qbj1"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "idCol", null));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicJoinEntity.class, "qbj1"))
                    .join(QueryJoin.entityType(QueryBasicJoinEntity.class, QueryBasicEntity.class, "qb1"))
                    .select(QuerySelection.field(QueryBasicJoinEntity.class, "idCol", null));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .join(QueryJoin.entityType(QueryBasicEntity.class, QueryBasicJoinEntity.class, "qbj1",
                                               QueryJoinType.LEFT))
                    .select(QuerySelection.field(QueryBasicEntity.class, "idCol", null));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb1"))
                    .join(QueryJoin.entityType(QueryBasicEntity.class
                            , QueryBasicDetailsEntity.class
                            , JoinExpr.build()
                                .field(QueryBasicEntity.class,"name").eq().field(QueryBasicDetailsEntity.class,"name")
                            , "qbd1"))
                    .select(QuerySelection.field(QueryBasicDetailsEntity.class, "description", null));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .groupBy(QueryGroup.rawSql("name"))
                    .select(QuerySelection.rawSql("name"));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb"))
                    .groupBy(QueryGroup.field(QueryBasicEntity.class, "name"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "name", null));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .groupBy(QueryGroup.rawSql("name"))
                    .having(QueryGroupCondition.rawSql("count(id_col)>1"))
                    .select(QuerySelection.rawSql("name"));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.entityType(QueryBasicEntity.class, "qb"))
                    .select(QuerySelection.field(QueryBasicEntity.class, "name", null))
                    .groupBy(QueryGroup.field(QueryBasicEntity.class, "name"))
                    .having(QueryGroupCondition.expression(
                            GroupConditionExpr.build()
                                .field(QueryBasicEntity.class, "name").count().gt().value(ColumnType.INTEGER,1)
                    ));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .orderBy(QueryOrderBy.rawSql("name"))
                    .select(QuerySelection.rawSql("name"));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .orderBy(QueryOrderBy.field(QueryBasicEntity.class, "name"))
                    .orderBy(QueryOrderBy.field(QueryBasicEntity.class,"idCol"))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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
            ITransaction tx = connector.createTransaction();
            createTestData(tx);
            tx.commit();
            tx.close();

            tx = connector.createTransaction();
            ISelectionQuery query = new SelectionQuery()
                    .from(QueryFrom.rawSql("query_basic qb1"))
                    .orderBy(QueryOrderBy.field(QueryBasicEntity.class, "name", QueryOrderType.DESCEND))
                    .orderBy(QueryOrderBy.field(QueryBasicEntity.class, "idCol", QueryOrderType.DESCEND))
                    .select(QuerySelection.entityType(QueryBasicEntity.class));

            Collection results = query.toList(tx);
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

    private void createTestData(ITransaction tx) throws PersistException
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
            entity.persist(tx);
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
            detailsEntity.persist(tx);
            detailsEntities.add(detailsEntity);
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
