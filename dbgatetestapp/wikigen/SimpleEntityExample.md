# Simple Example
Example is created using derby as the database engine. This demonstrates 
basic features like persisting, retrieval, change tracking

# Details
### Create the entity

    
	@TableInfo(tableName = "simple_entity")
	public class SimpleEntity  extends DefaultEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int id;
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String name;
	
	    public SimpleEntity()
	    {
	    }
	
	    public int getId()
	    {
	        return id;
	    }
	
	    public void setId(int id)
	    {
	        this.id = id;
	    }
	
	    public String getName()
	    {
	        return name;
	    }
	
	    public void setName(String name)
	    {
	        this.name = name;
	    }
	}

### Base class for examples

    
	public class ExampleBase
	{
	    protected ITransactionFactory factory;
	    protected String dbName = "testdb";
	
	    public void initializeConnector()
	    {
	        try
	        {
	            factory = new DefaultTransactionFactory(() -> {
	                try {
	                    Logger.getLogger(getClass().getName()).info("Starting in-memory database for unit tests");
	                    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	                    return DriverManager.getConnection(String.format("jdbc:derby:memory:%s;create=true", dbName));
	                }
	                catch (Exception ex){
	                    Logger.getLogger(getClass().getName()).severe(String.format("Exception during database %s startup.",dbName));
	                    return null;
	                }
	            }, DefaultTransactionFactory.DB_DERBY);
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	            Logger.getLogger(getClass().getName()).severe("Exception during database startup.");
	        }
	    }
	
	    public void destroyConnector()
	    {
	        try
	        {
	            DriverManager.getConnection(String.format("jdbc:derby:memory:%s;shutdown=true",dbName)).close();
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
	            VFMemoryStorageFactory.purgeDatabase(new File(dbName).getCanonicalPath());
	        }
	        catch (IOException iox)
	        {
	            iox.printStackTrace();
	        }
	        ((DefaultTransactionFactory)factory).finalize();
	        factory = null;
	    }
	}

### Class to do the operation

    
	public class SimpleExample extends ExampleBase
	{
	    private int id = 43;
	
	    public SimpleExample()
	    {
	        dbName = "simple_example";
	    }
	
	    public SimpleEntity createEntity()
	    {
	        SimpleEntity entity = new SimpleEntity();
	        entity.setId(id);
	        entity.setName("Entity");
	        return entity;
	    }
	
	    public void patch() throws DBPatchingException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        Collection<Class> entityTypes = new ArrayList<Class>();
	        entityTypes.add(SimpleEntity.class);
	        factory.getDbGate().patchDataBase(tx,entityTypes,false);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public void persist(SimpleEntity entity) throws PersistException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        entity.persist(tx);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public SimpleEntity retrieve() throws SQLException,RetrievalException
	            ,TransactionCreationFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        PreparedStatement ps = tx.getConnection().prepareStatement("select * from simple_entity where id = ?");
	        ps.setInt(1,id);
	        ResultSet rs = ps.executeQuery();
	        SimpleEntity entity = null;
	        if (rs.next())
	        {
	            entity = new SimpleEntity();
	            entity.retrieve(rs,tx);
	        }
	        DBMgtUtility.close(rs);
	        DBMgtUtility.close(ps);
	        DBMgtUtility.close(tx);
	        return entity;
	    }
	
	    public SimpleEntity retrieveWithQuery() throws SQLException,RetrievalException
	            ,TransactionCreationFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        ISelectionQuery query = new SelectionQuery()
	                .from(QueryFrom.type(SimpleEntity.class))
	                .select(QuerySelection.type(SimpleEntity.class));
	
	        Collection entities = query.toList(tx);
	        DBMgtUtility.close(tx);
	
	        return (SimpleEntity)entities.iterator().next();
	    }
	
	    public static void main(String[] args)
	    {
	        SimpleExample simpleExample = new SimpleExample();
	        simpleExample.initializeConnector();
	        try
	        {
	            simpleExample.patch();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            SimpleEntity entity = simpleExample.createEntity();
	            simpleExample.persist(entity);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            SimpleEntity entity = simpleExample.retrieveWithQuery();
	            System.out.println("entity.Name = " + entity.getName());
	
	            entity.setName("Updated");
	            simpleExample.persist(entity);
	            entity = simpleExample.retrieve();
	            System.out.println("entity.Name = " + entity.getName());
	
	            entity.setStatus(EntityStatus.DELETED);
	            simpleExample.persist(entity);
	            entity = simpleExample.retrieve();
	            System.out.println("entity = " + entity);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        simpleExample.destroyConnector();
	    }
	}
￿