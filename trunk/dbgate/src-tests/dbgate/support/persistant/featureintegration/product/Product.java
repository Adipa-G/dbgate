package dbgate.support.persistant.featureintegration.product;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.TableInfo;

/**
 * Date: Mar 31, 2011
 * Time: 9:46:32 PM
 */
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
