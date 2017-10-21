# Introduction

This demonstrates one to one relationship capability of the library using 2 types of different child objects.

# Details
### Parent class

    
	@TableInfo(tableName = "parent_entity")
	public class One2OneParentEntity extends DefaultEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int id;
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String name;
	    @ForeignKeyInfoList(infoList = {
	        @ForeignKeyInfo(name = "parent2child_a"
	                    ,relatedObjectType = One2OneChildEntityA.class
	                    ,updateRule = ReferentialRuleType.RESTRICT
	                    ,deleteRule = ReferentialRuleType.CASCADE
	                    ,fieldMappings =  {@ForeignKeyFieldMapping(fromField = "id",toField = "parentId")}),
	        @ForeignKeyInfo(name = "parent2child_b"
	                ,relatedObjectType = One2OneChildEntityB.class
	                ,updateRule = ReferentialRuleType.RESTRICT
	                ,deleteRule = ReferentialRuleType.CASCADE
	                ,fieldMappings =  {@ForeignKeyFieldMapping(fromField = "id",toField = "parentId")})
	    })
	    private One2OneChildEntity childEntity;
	
	    public One2OneParentEntity()
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
	
	    public One2OneChildEntity getChildEntity()
	    {
	        return childEntity;
	    }
	
	    public void setChildEntity(One2OneChildEntity one2OneChildEntity)
	    {
	        this.childEntity = one2OneChildEntity;
	    }
	}

### Child Base Class

    
	public abstract class One2OneChildEntity extends DefaultEntity
	{
	    public abstract String getName();
	
	    public abstract void setName(String name);
	}

### Child Sub Class 1

    
	@TableInfo(tableName = "child_entity_a")
	public class One2OneChildEntityA extends One2OneChildEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int parentId;
	
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String name;
	
	    public int getParentId()
	    {
	        return parentId;
	    }
	
	    public void setParentId(int parentId)
	    {
	        this.parentId = parentId;
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

### Child Sub Class 2

    
	@TableInfo(tableName = "child_entity_b")
	public class One2OneChildEntityB extends One2OneChildEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int parentId;
	
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String name;
	
	    public int getParentId()
	    {
	        return parentId;
	    }
	
	    public void setParentId(int parentId)
	    {
	        this.parentId = parentId;
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

### Testing Class
get the source for ExampleBase from https://github.com/Adipa-G/dbgate/wiki/SimpleEntityExample

    
	public class One2OneExample extends ExampleBase
	{
	    private static int idA = 43;
	    private static int idB = 44;
	
	    public One2OneExample()
	    {
	        dbName = "one_2_one_example";
	    }
	
	    public One2OneParentEntity createEntityWithChildA()
	    {
	        One2OneParentEntity entity = new One2OneParentEntity();
	        entity.setId(idA);
	        entity.setName("Parent");
	
	        entity.setChildEntity(new One2OneChildEntityA());
	        entity.getChildEntity().setName("Child A");
	
	        return entity;
	    }
	
	    public One2OneParentEntity createEntityWithChildB()
	    {
	        One2OneParentEntity entity = new One2OneParentEntity();
	        entity.setId(idB);
	        entity.setName("Parent");
	
	        entity.setChildEntity(new One2OneChildEntityB());
	        entity.getChildEntity().setName("Child B");
	
	        return entity;
	    }
	
	    public void patch() throws DBPatchingException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        Collection<Class> entityTypes = new ArrayList<Class>();
	        entityTypes.add(One2OneParentEntity.class);
	        entityTypes.add(One2OneChildEntityA.class);
	        entityTypes.add(One2OneChildEntityB.class);
	        factory.getDbGate().patchDataBase(tx, entityTypes, false);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public void persistWithA() throws PersistException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        One2OneParentEntity entity = createEntityWithChildA();
	        entity.persist(tx);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public void persistWithB() throws PersistException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        One2OneParentEntity entity = createEntityWithChildB();
	        entity.persist(tx);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public One2OneParentEntity retrieve(int id) throws SQLException,RetrievalException
	            ,TransactionCreationFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        PreparedStatement ps = tx.getConnection().prepareStatement("select * from parent_entity where id = ?");
	        ps.setInt(1, id);
	        ResultSet rs = ps.executeQuery();
	        One2OneParentEntity entity = null;
	        if (rs.next())
	        {
	            entity = new One2OneParentEntity();
	            entity.retrieve(rs,tx);
	        }
	        DBMgtUtility.close(rs);
	        DBMgtUtility.close(ps);
	        DBMgtUtility.close(tx);
	        return entity;
	    }
	
	    public static void main(String[] args)
	    {
	        One2OneExample inheritanceExample = new One2OneExample();
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
	            inheritanceExample.persistWithA();
	            inheritanceExample.persistWithB();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            One2OneParentEntity entity = inheritanceExample.retrieve(idA);
	            System.out.println("entityA.Name() = " + entity.getName());
	            System.out.println("entityA.ChildName() = " + entity.getChildEntity().getName());
	
	            entity = inheritanceExample.retrieve(idB);
	            System.out.println("entityB.Name() = " + entity.getName());
	            System.out.println("entityB.ChildName() = " + entity.getChildEntity().getName());
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        inheritanceExample.destroyConnector();
	    }
	}
￿