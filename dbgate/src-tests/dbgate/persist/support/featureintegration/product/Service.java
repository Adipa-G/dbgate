package dbgate.persist.support.featureintegration.product;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.TableInfo;

/**
 * Date: Mar 31, 2011
 * Time: 9:46:42 PM
 */
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
