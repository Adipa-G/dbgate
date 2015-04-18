#Introduction
In short dbgate is an object relational mapping framework. What it differs from other ORM frameworks is that it gives the complete control over how the persistence/retrieval takes place at entity level. This is achieved by means of overriding/implementing methods provided in the base entity classes. 

Also it can be configured to be smart, where it can handle core functions of a modern ORM like data integrity checks/change tracking. 
 
###Features
* Core relationships (One to One, One to Many, Inheritance)
* Change tracking
* Data verification on persisting
* Lazy loading
* Data migration
* Strong query language
* Parallel operation with multiple databases
* Easily used with legacy databases

###Using the library
Run time following dependencies are needed
* cglib-nodep-2.2.2

###Quick Start
####Define Entities
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

####Persisting
Persisting is straightforward

	SimpleEntity entity = new SimpleEntity();
	//set values to the entity
	entity.persist(tx);  //tx is dbgate.ITransaction which created using dbgate.ITransactionFactory 

####Retrieving
To retrieve an entity from the database without using dbgate queries, there must be a resultset pointing to the record to fetch.

	SimpleEntity entity = new SimpleEntity();
	//set values to the entity
	entity.retrieve(rs,tx);  //use the java.sql.ResultSet to the record and tx is dbgate.ITransaction which created using dbgate.ITransactionFactory

####Data Migration
Data migration pretty easy

	Collection<Class> entityTypes = new ArrayList<Class>();
	entityTypes.add(SimpleEntity.class);
	DbGate.getSharedInstance().patchDataBase(tx,entityTypes,false); //if the last parameter is true it would drop all the existing tables

####Strong queries
Strong queries have support for many complex scenarios like sub queries, unions and group conditions. However for simplicity only a basic example is listed below

	ISelectionQuery query = new SelectionQuery()
					.from(QueryFrom.type(SimpleEntity.class))
					.select(QuerySelection.type(SimpleEntity.class));
	Collection entities = query.toList(tx);

More examples can be found in the wiki. Also there is a sample project using the library available in the sources named ermanagementtestapp.

####Setting up for development
If you are interested in contributing please drop an email

Fastest way to setup the system is to
* checkout the project,
* generate the ant build from the IDE
* run the build.xml script

####Compile From Sources
The editor used is Intellij IDEA Community Edition. An ant script is included to run the unit tests. Compilation ant script needed to be re-generated from the editor to run the build. Required libraries are included in the lib folder.

####Things to be done
* Basic performance tests
* Support for batching
* Fluent interface for manual entity registration
* Ability to switch between fields/methods to set values to objects (instead of calling  setter, setting value directly to the field)
* Get rid of requirement of super class (support for POJO entities)
* Ability to add events
* Ability to use a discriminator column to determine the sub class type when loading entities form different tables
* Conventions to automatically configure the mappings
* Add support for more data types such as clob/blob
* Library currently supports limited no of database types at sql generation level (specially queries for db meta data), so that part need to be expanded for more databases.
