package dbgate.support.persistant.nonidentifyingrelationwithoutcolumn;

import dbgate.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/2/12
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
@TableInfo(tableName = "relation_test_product")
public class Product extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int productId;
    @ColumnInfo(columnType = ColumnType.DOUBLE)
    private double price;
    @ForeignKeyInfo(name = "product2currency"
        ,relatedObjectType = Currency.class
        ,updateRule = ReferentialRuleType.RESTRICT
        ,deleteRule = ReferentialRuleType.CASCADE
        ,nonIdentifyingRelation = true
        ,nullable = true
        ,fieldMappings = {@ForeignKeyFieldMapping(fromField = "productCurrencyId", toField = "currencyId")})
    private Currency currency;

    public int getProductId()
    {
        return productId;
    }

    public void setProductId(int productId)
    {
        this.productId = productId;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }
}
