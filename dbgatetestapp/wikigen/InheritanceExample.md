# Introduction

This demonstrates inheritance capability of the library using 3 level inheritance structure.

# Details
### Top class

    
	@TableInfo(tableName = "top_entity")
	public class TopEntity extends DefaultEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true,subClassCommonColumn = true)
	    private int id;
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String superName;
	
	    public TopEntity()
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
	
	    public String getSuperName()
	    {
	        return superName;
	    }
	
	    public void setSuperName(String superName)
	    {
	        this.superName = superName;
	    }
	}

### Middle Class

    
	@TableInfo(tableName = "middle_entity")
	public class MiddleEntity extends TopEntity
	{
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String middleName;
	
	    public MiddleEntity()
	    {
	    }
	
	    public String getMiddleName()
	    {
	        return middleName;
	    }
	
	    public void setMiddleName(String middleName)
	    {
	        this.middleName = middleName;
	    }
	}

### Bottom Class

    
	@TableInfo(tableName = "bottom_entity")
	public class BottomEntity extends MiddleEntity
	{
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String subName;
	
	    public BottomEntity()
	    {
	    }
	
	    public String getSubName()
	    {
	        return subName;
	    }
	
	    public void setSubName(String subName)
	    {
	        this.subName = subName;
	    }
	}

### Testing Class
get the source for ExampleBase from https://github.com/Adipa-G/dbgate/wiki/SimpleEntityExample

    
	public class InheritanceExample extends ExampleBase
	{
	    private int id = 43;
	
	    public InheritanceExample()
	    {
	        dbName = "inheritance_example";
	    }
	
	    public BottomEntity createEntity()
	    {
	        BottomEntity entity = new BottomEntity();
	        entity.setId(id);
	        entity.setSuperName("Super");
	        entity.setMiddleName("Middle");
	        entity.setSubName("Sub");
	        return entity;
	    }
	
	    public void patch() throws DBPatchingException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        Collection<Class> entityTypes = new ArrayList<Class>();
	        entityTypes.add(TopEntity.class);
	        entityTypes.add(BottomEntity.class);
	        entityTypes.add(MiddleEntity.class);
	        factory.getDbGate().patchDataBase(tx, entityTypes, false);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public void persist() throws PersistException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        BottomEntity entity = createEntity();
	        entity.persist(tx);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public BottomEntity retrieve() throws SQLException,RetrievalException
	            ,TransactionCreationFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        PreparedStatement ps = tx.getConnection().prepareStatement("select * from bottom_entity where id = ?");
	        ps.setInt(1,id);
	        ResultSet rs = ps.executeQuery();
	        BottomEntity entity = null;
	        if (rs.next())
	        {
	            entity = new BottomEntity();
	            entity.retrieve(rs,tx);
	        }
	        DBMgtUtility.close(rs);
	        DBMgtUtility.close(ps);
	        DBMgtUtility.close(tx);
	        return entity;
	    }
	
	    public static void main(String[] args)
	    {
	        InheritanceExample inheritanceExample = new InheritanceExample();
	        inheritanceExample.initializeConnector();
	        try
	        {
	            inheritanceExample.patch();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            inheritanceExample.persist();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            BottomEntity entity = inheritanceExample.retrieve();
	            System.out.println("entity.SuperName = " + entity.getSuperName());
	            System.out.println("entity.MiddleName = " + entity.getMiddleName());
	            System.out.println("entity.SubName = " + entity.getSubName());
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        inheritanceExample.destroyConnector();
	    }
	}
￿