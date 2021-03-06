# Introduction

This demonstrates no of features working together, with reverse relationships and non-identifying relationships.

# Details
### Item class

    
	@TableInfo(tableName = "product_item")
	public abstract class Item extends DefaultEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true,subClassCommonColumn = true)
	    private int itemId;
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String name;
	
	    public Item()
	    {
	    }
	
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

### Product Class

    
	@TableInfo(tableName = "product_product")
	public class Product extends Item
	{
	    @ColumnInfo(columnType = ColumnType.DOUBLE)
	    private double unitPrice;
	    @ColumnInfo(columnType = ColumnType.DOUBLE,nullable = true)
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

### Service Class

    
	@TableInfo(tableName = "product_service")
	public class Service extends Item
	{
	    @ColumnInfo(columnType = ColumnType.DOUBLE)
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

### Transaction Class

    
	@TableInfo(tableName = "order_transaction")
	public class Transaction extends DefaultEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int transactionId;
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String name;
	    @ForeignKeyInfo(name = "tx2item_tx",
	            relatedObjectType = ItemTransaction.class,
	            updateRule = ReferentialRuleType.RESTRICT,
	            deleteRule = ReferentialRuleType.CASCADE,
	            fieldMappings =  {@ForeignKeyFieldMapping(fromField = "transactionId",toField = "transactionId")})
	    private Collection<ItemTransaction> itemTransactions;
	
	    public Transaction()
	    {
	        itemTransactions = new ArrayList<ItemTransaction>();
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
	
	    public void setItemTransactions(Collection<ItemTransaction> itemTransactions)
	    {
	        this.itemTransactions = itemTransactions;
	    }
	}

### ItemTransaction Class

    
	@TableInfo(tableName = "order_item_transaction")
	public class ItemTransaction  extends DefaultEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int transactionId;
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int indexNo;
	    @ColumnInfo(columnType = ColumnType.INTEGER)
	    private int itemId;
	    @ForeignKeyInfoList(infoList = {
	    @ForeignKeyInfo(name = "item_tx2item",
	            relatedObjectType = Item.class,
	            updateRule = ReferentialRuleType.RESTRICT,
	            deleteRule = ReferentialRuleType.CASCADE,
	            nonIdentifyingRelation = true,
	            fieldMappings =  {@ForeignKeyFieldMapping(fromField = "itemId",toField = "itemId")})
	    })
	    private Item item;
	    @ForeignKeyInfo(name = "item_tx2tx_rev",
	            relatedObjectType = Transaction.class,
	            reverseRelation = true,
	            fieldMappings =  {@ForeignKeyFieldMapping(fromField = "transactionId",toField = "transactionId")})
	    private Transaction transaction;
	    @ForeignKeyInfo(name = "tx2item_tx_charge",
	            relatedObjectType = ItemTransactionCharge.class,
	            updateRule = ReferentialRuleType.RESTRICT,
	            deleteRule = ReferentialRuleType.CASCADE,
	            fieldMappings =  {@ForeignKeyFieldMapping(fromField = "transactionId",toField = "transactionId"),
	                              @ForeignKeyFieldMapping(fromField = "indexNo",toField = "indexNo")})
	    private Collection<ItemTransactionCharge> itemTransactionCharges;
	
	    public ItemTransaction()
	    {
	    }
	
	    public ItemTransaction(Transaction transaction)
	    {
	        this.transaction = transaction;
	        itemTransactionCharges = new ArrayList<ItemTransactionCharge>();
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
	
	    public int getItemId()
	    {
	        return itemId;
	    }
	
	    public void setItemId(int itemId)
	    {
	        this.itemId = itemId;
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
	
	    public void setItemTransactionCharges(Collection<ItemTransactionCharge> itemTransactionCharges)
	    {
	        this.itemTransactionCharges = itemTransactionCharges;
	    }
	}

### ItemTransactionCharge Class

    
	@TableInfo(tableName = "order_item_transaction_charge")
	public class ItemTransactionCharge  extends DefaultEntity
	{
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int transactionId;
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int indexNo;
	    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
	    private int chargeIndex;
	    @ColumnInfo(columnType = ColumnType.VARCHAR)
	    private String chargeCode;
	    @ForeignKeyInfo(name = "item_tx_charge2tx_rev",
	            relatedObjectType = Transaction.class,
	            reverseRelation = true,
	            fieldMappings =  {@ForeignKeyFieldMapping(fromField = "transactionId",toField = "transactionId")})
	    private Transaction transaction;
	    @ForeignKeyInfo(name = "item_tx_charge2tx_item_rev",
	            relatedObjectType = ItemTransaction.class,
	            reverseRelation = true,
	            fieldMappings =  {@ForeignKeyFieldMapping(fromField = "transactionId",toField = "transactionId"),
	                              @ForeignKeyFieldMapping(fromField = "indexNo",toField = "indexNo")})
	    private ItemTransaction itemTransaction;
	
	    public ItemTransactionCharge()
	    {
	    }
	
	    public ItemTransactionCharge(ItemTransaction itemTransaction)
	    {
	        this.itemTransaction = itemTransaction;
	        this.transaction = itemTransaction.getTransaction();
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
	
	    public Transaction getTransaction()
	    {
	        return transaction;
	    }
	
	    public void setTransaction(Transaction transaction)
	    {
	        this.transaction = transaction;
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

### Testing Class
get the source for ExampleBase from https://github.com/Adipa-G/dbgate/wiki/SimpleEntityExample

    
	public class ComplexExample extends ExampleBase
	{
	    private static int productId = 321;
	    private static int serviceId = 322;
	    private static int transactionId = 43;
	
	    public ComplexExample()
	    {
	        dbName = "complex_example";
	    }
	
	    public Product createDefaultProduct() throws PersistException
	            ,TransactionCreationFailedException
	    {
	        Product product = new Product();
	        product.setItemId(productId);
	        product.setName("Product");
	        product.setUnitPrice(54D);
	        ITransaction tx = factory.createTransaction();
	        product.persist(tx);
	        DBMgtUtility.close(tx);
	        return product;
	    }
	
	    public Service createDefaultService() throws PersistException
	            ,TransactionCreationFailedException
	    {
	        Service service = new Service();
	        service.setItemId(serviceId);
	        service.setName("Service");
	        service.setHourlyRate(65D);
	        ITransaction tx = factory.createTransaction();
	        service.persist(tx);
	        DBMgtUtility.close(tx);
	        return service;
	    }
	
	    public Transaction createDefaultTransaction() throws PersistException
	            ,TransactionCreationFailedException
	    {
	        Transaction transaction = new Transaction();
	        transaction.setTransactionId(transactionId);
	        transaction.setName("TRS-0001");
	
	        ItemTransaction productTransaction = new ItemTransaction(transaction);
	        productTransaction.setIndexNo(0);
	        productTransaction.setItem(createDefaultProduct());
	        transaction.getItemTransactions().add(productTransaction);
	
	        ItemTransactionCharge productTransactionCharge = new ItemTransactionCharge(productTransaction);
	        productTransactionCharge.setChargeCode("Product-Sell-Code");
	        productTransaction.getItemTransactionCharges().add(productTransactionCharge);
	
	        ItemTransaction serviceTransaction = new ItemTransaction(transaction);
	        serviceTransaction.setIndexNo(0);
	        serviceTransaction.setItem(createDefaultService());
	        transaction.getItemTransactions().add(serviceTransaction);
	
	        ItemTransactionCharge serviceTransactionCharge = new ItemTransactionCharge(serviceTransaction);
	        serviceTransactionCharge.setChargeCode("Service-Sell-Code");
	        serviceTransaction.getItemTransactionCharges().add(serviceTransactionCharge);
	
	        ITransaction tx = factory.createTransaction();
	        transaction.persist(tx);
	        DBMgtUtility.close(tx);
	        return transaction;
	    }
	
	    public void patch() throws DBPatchingException, SQLException
	            ,TransactionCreationFailedException,TransactionCommitFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        Collection<Class> entityTypes = new ArrayList<Class>();
	        entityTypes.add(Transaction.class);
	        entityTypes.add(ItemTransaction.class);
	        entityTypes.add(ItemTransactionCharge.class);
	        entityTypes.add(Product.class);
	        entityTypes.add(Service.class);
	        factory.getDbGate().patchDataBase(tx, entityTypes, false);
	        tx.commit();
	        DBMgtUtility.close(tx);
	    }
	
	    public Transaction retrieve(int id) throws SQLException,RetrievalException
	            ,TransactionCreationFailedException
	    {
	        ITransaction tx = factory.createTransaction();
	        PreparedStatement ps = tx.getConnection().prepareStatement(
	                "select * from order_transaction where transaction_Id = ?");
	        ps.setInt(1, id);
	        ResultSet rs = ps.executeQuery();
	        Transaction entity = null;
	        if (rs.next())
	        {
	            entity = new Transaction();
	            entity.retrieve(rs,tx);
	        }
	        DBMgtUtility.close(rs);
	        DBMgtUtility.close(ps);
	        DBMgtUtility.close(tx);
	        return entity;
	    }
	
	    public static void main(String[] args)
	    {
	        ComplexExample complexExample = new ComplexExample();
	        complexExample.initializeConnector();
	        try
	        {
	            complexExample.patch();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            complexExample.createDefaultTransaction();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            Transaction entity = complexExample.retrieve(transactionId);
	            System.out.println("entityA.Name() = " + entity.getName());
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        complexExample.destroyConnector();
	    }
	}￿