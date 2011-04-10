package dbgate.ermanagement.support.persistant.featureintegration.product;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;

/**
 * Date: Mar 31, 2011
 * Time: 9:46:32 PM
 */
@DBTableInfo(tableName = "product_product")
public class Product extends Item
{
    @DBColumnInfo(columnType = DBColumnType.DOUBLE)
    private double unitPrice;
    @DBColumnInfo(columnType = DBColumnType.DOUBLE,nullable = true)
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
