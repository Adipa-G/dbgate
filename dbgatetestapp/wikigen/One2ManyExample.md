# Introduction

This demonstrates one to many relationship capability of the library using 2 types of different child objects.

# Details
### Parent class

    
	@TableInfo(tableName = "parent_entity")
	public class One2ManyParentEntity extends DefaultEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int id;
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String name;
	    @ForeignKeyInfoList(infoList = {
	        @ForeignKeyInfo(name = "parent2childA",
	                relatedObjectType = One2ManyChildEntityA.class,
	                updateRule = ReferentialRuleType.RESTRICT,
	                deleteRule = ReferentialRuleType.CASCADE,
	                fieldMappings =  {@ForeignKeyFieldMapping(fromField = "id",toField = "parentId")})
	        ,
	        @ForeignKeyInfo(name = "parent2childB",
	                relatedObjectType = One2ManyChildEntityB.class,
	                updateRule = ReferentialRuleType.RESTRICT,
	                deleteRule = ReferentialRuleType.CASCADE,
	                fieldMappings =  {@ForeignKeyFieldMapping(fromField = "id",toField = "parentId")})
	    })
	    private Collection<One2ManyChildEntity> childEntities;
	
	    public One2ManyParentEntity()
	    {
	        childEntities = new ArrayList<One2ManyChildEntity>();
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
	
	    public Collection<One2ManyChildEntity> getChildEntities()
	    {
	        return childEntities;
	    }
	
	    public void setChildEntities(Collection<One2ManyChildEntity> childEntities)
	    {
	        this.childEntities = childEntities;
	    }
	}

### Child Base Class

    
	public abstract class One2ManyChildEntity extends DefaultEntity
	{
	    public abstract String getName();
	
	    public abstract void setName(String name);
	}

### Child Sub Class 1

    
	@TableInfo(tableName = "child_entity_a")
	public class One2ManyChildEntityA extends One2ManyChildEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int parentId;
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int indexNo;
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
	
	    public int getIndexNo()
	    {
	        return indexNo;
	    }
	
	    public void setIndexNo(int indexNo)
	    {
	        this.indexNo = indexNo;
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
	public class One2ManyChildEntityB extends One2ManyChildEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int parentId;
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int indexNo;
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
	
	    public int getIndexNo()
	    {
	        return indexNo;
	    }
	
	    public void setIndexNo(int indexNo)
	    {
	        this.indexNo = indexNo;
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

    
	public class One2ManyExample extends ExampleBase
	{
	    private static int id = 43;
	
	    public One2ManyExample()
	    {
	        dbName = "one_2_many_example";
	    }
	
	    public One2ManyParentEntity createEntityWithChildern()
	    {
	        One2ManyParentEntity entity = new One2ManyParentEntity();
	        entity.setId(id);
	        entity.setName("Parent");
	
	        One2ManyChildEntityA childEntityA = new One2ManyChildEntityA();
	        childEntityA.setName("Child A");
	        entity.getChildEntities().add(childEntityA);
	
	        One2ManyChildEntityB childEntityB = new One2ManyChildEntityB();
	        childEntityB.setName("Child B");
	        entity.getChildEntities().add(childEntityB);
	
	        return entity;
	    }
	
	    public void patch() throws DBPatchingException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        Collection<Class> entityTypes = new ArrayList<Class>();
	        entityTypes.add(One2ManyParentEntity.class);
	        entityTypes.add(One2ManyChildEntityA.class);
	        entityTypes.add(One2ManyChildEntityB.class);
	        factory.getDbGate().patchDataBase(tx, entityTypes, false);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public void persist() throws PersistException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        One2ManyParentEntity entity = createEntityWithChildern();
	        entity.persist(tx);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public One2ManyParentEntity retrieve(int id) throws SQLException,RetrievalException
	            ,TransactionCreationFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        PreparedStatement ps = tx.getConnection().prepareStatement("select * from parent_entity where id = ?");
	        ps.setInt(1, id);
	        ResultSet rs = ps.executeQuery();
	        One2ManyParentEntity entity = null;
	        if (rs.next())
	        {
	            entity = new One2ManyParentEntity();
	            entity.retrieve(rs,tx);
	        }
	        DBMgtUtility.close(rs);
	        DBMgtUtility.close(ps);
	        DBMgtUtility.close(tx);
	        return entity;
	    }
	
	    public static void main(String[] args)
	    {
	        One2ManyExample one2ManyExample = new One2ManyExample();
	        one2ManyExample.initializeConnector();
	        try
	        {
	            one2ManyExample.patch();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            one2ManyExample.persist();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            One2ManyParentEntity entity = one2ManyExample.retrieve(id);
	            System.out.println("entity.Name() = " + entity.getName());
	            for (One2ManyChildEntity childEntity : entity.getChildEntities())
	            {
	                System.out.println("entity.ChildName() = " + childEntity.getName());
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        one2ManyExample.destroyConnector();
	    }
	}
￿