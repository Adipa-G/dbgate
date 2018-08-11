# Introduction
In short dbgate is an object relational mapping framework. What it differs from other ORM frameworks is that it gives the complete control over how the persistence/retrieval takes place at entity level. This is achieved by means of overriding/implementing methods provided in the base entity classes. 

Also it can be configured to be smart, where it can handle core functions of a modern ORM like data integrity checks/change tracking. 
 
### Features
* Core relationships (One to One, One to Many, Inheritance)
* Change tracking
* Data verification on persisting
* Lazy loading
* Data migration
* Strong query language
* Parallel operation with multiple databases
* Easily used with legacy databases

### Using the library
Run time following dependencies are needed
* cglib-nodep-2.2.2

### Quick Start
#### Define Entities
There are 3 ways to define entities
* Use annotations
* Extending an abstract class
* Registering entities manually

However for ease of understanding here only the annotations based approach would be used.All the entities has to be implemented the interface 
>IEntity.

The class
>DefaultEntity

is designed to used as super class for any entity.

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

Above class is a entity definition for a class with only 2 columns, first is named id and is a key, latter is a varchar column. Annotation
>@TableInfo 

is used to define the name of the table the class supposed to be mapped. 
>@ColumnInfo

is used to define the table column which the field is supposed to mapped.

#### Persisting
Persisting is straightforward

	SimpleEntity entity = new SimpleEntity();
	//set values to the entity
	entity.persist(tx);  //tx is dbgate.ITransaction which created using dbgate.ITransactionFactory 

#### Retrieving
To retrieve an entity from the database without using dbgate queries, there must be a resultset pointing to the record to fetch.

	SimpleEntity entity = new SimpleEntity();
	//set values to the entity
	entity.retrieve(rs,tx);  //use the java.sql.ResultSet to the record and tx is dbgate.ITransaction which created using dbgate.ITransactionFactory

#### Data Migration
Data migration pretty easy

	Collection<Class> entityTypes = new ArrayList<Class>();
	entityTypes.add(SimpleEntity.class);
	DbGate.getSharedInstance().patchDataBase(tx,entityTypes,false); //if the last parameter is true it would drop all the existing tables

#### Strong queries
Strong queries have support for many complex scenarios like sub queries, unions and group conditions. However for simplicity only a basic example is listed below

	ISelectionQuery query = new SelectionQuery()
					.from(QueryFrom.entityType(SimpleEntity.class))
					.select(QuerySelection.entityType(SimpleEntity.class));
	Collection entities = query.toList(tx);

More examples can be found in the wiki. Also there is a sample project using the library available in the sources named ermanagementtestapp.

#### Setting up for development
If you are interested in contributing please drop an email

Fastest way to setup the system is to
* checkout the project,
* generate the ant build from the IDE
* run the build.xml script

#### Compile From Sources
The editor used is Intellij IDEA Community Edition. An ant script is included to run the unit tests. Compilation ant script needed to be re-generated from the editor to run the build. Required libraries are included in the lib folder.

### Performance
#### Test entities

	public abstract class Item
    {
        private int itemId;
        private String name;

        public int getItemId()
        {
            return itemId;
        }

        public void setItemId(int itemId)
        {
            this.itemId = itemId;
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

	public class Product extends Item
    {
        private double unitPrice;
        private Double bulkUnitPrice;

        public double getUnitPrice()
        {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice)
        {
            this.unitPrice = unitPrice;
        }

        public Double getBulkUnitPrice()
        {
            return bulkUnitPrice;
        }

        public void setBulkUnitPrice(Double bulkUnitPrice)
        {
            this.bulkUnitPrice = bulkUnitPrice;
        }
    }

	public class Service extends Item
    {
        private double hourlyRate;

        public double getHourlyRate()
        {
            return hourlyRate;
        }

        public void setHourlyRate(double hourlyRate)
        {
            this.hourlyRate = hourlyRate;
        }
    }

	public class Transaction
    {
    	private int transactionId;
    	private String name;
    	private Set<ItemTransaction> itemTransactions;

    	public Transaction()
    	{
    		this.itemTransactions = new HashSet<>();
    	}

    	public int getTransactionId()
    	{
    		return transactionId;
    	}

    	public void setTransactionId(int transactionId)
    	{
    		this.transactionId = transactionId;
    	}

    	public String getName()
    	{
    		return name;
    	}

    	public void setName(String name)
    	{
    		this.name = name;
    	}

    	public Collection<ItemTransaction> getItemTransactions()
    	{
    		return itemTransactions;
    	}

    	public void setItemTransactions(Set<ItemTransaction> itemTransactions)
    	{
    		this.itemTransactions = itemTransactions;
    	}
    }

	public class ItemTransaction implements Serializable
    {
        private int transactionId;
        private int indexNo;
        private Item item;
        private Transaction transaction;
        private Set<ItemTransactionCharge> itemTransactionCharges;

        public ItemTransaction()
        {
        }

        public ItemTransaction(Transaction transaction)
        {
            this.transaction = transaction;
            itemTransactionCharges = new HashSet<ItemTransactionCharge>();
        }

        public int getTransactionId()
        {
            return transactionId;
        }

        public void setTransactionId(int transactionId)
        {
            this.transactionId = transactionId;
        }

        public int getIndexNo()
        {
            return indexNo;
        }

        public void setIndexNo(int indexNo)
        {
            this.indexNo = indexNo;
        }

        public Item getItem()
        {
            return item;
        }

        public void setItem(Item item)
        {
            this.item = item;
        }

        public Transaction getTransaction()
        {
            return transaction;
        }

        public void setTransaction(Transaction transaction)
        {
            this.transaction = transaction;
        }

        public Collection<ItemTransactionCharge> getItemTransactionCharges()
        {
            return itemTransactionCharges;
        }

        public void setItemTransactionCharges(Set<ItemTransactionCharge> itemTransactionCharges)
        {
            this.itemTransactionCharges = itemTransactionCharges;
        }
    }

	public class ItemTransactionCharge implements Serializable
    {
        private int transactionId;
        private int indexNo;
        private int chargeIndex;
        private String chargeCode;
        private ItemTransaction itemTransaction;

        public ItemTransactionCharge()
        {
        }

        public ItemTransactionCharge(ItemTransaction itemTransaction)
        {
            this.itemTransaction = itemTransaction;
        }

        public int getTransactionId()
        {
            return transactionId;
        }

        public void setTransactionId(int transactionId)
        {
            this.transactionId = transactionId;
        }

        public int getIndexNo()
        {
            return indexNo;
        }

        public void setIndexNo(int indexNo)
        {
            this.indexNo = indexNo;
        }

        public int getChargeIndex()
        {
            return chargeIndex;
        }

        public void setChargeIndex(int chargeIndex)
        {
            this.chargeIndex = chargeIndex;
        }

        public String getChargeCode()
        {
            return chargeCode;
        }

        public void setChargeCode(String chargeCode)
        {
            this.chargeCode = chargeCode;
        }

        public ItemTransaction getItemTransaction()
        {
            return itemTransaction;
        }

        public void setItemTransaction(ItemTransaction itemTransaction)
        {
            this.itemTransaction = itemTransaction;
        }
    }

#### Test

Inserting/ Quering/ Updating/ Deleting 5000 `Transaction` entities using Hibernate (5.2.12) and DbGate. Test project is in the Repo.

#### Results (entities per second)

##### Hibernate
	Insertion :	897
	Querying :  995
	Update : 1550
	Delete	: 906

#### NDbGate
	Insertion :	711
	Querying :	630
	Update : 484
	Delete : 807

### License
GNU GPL V3