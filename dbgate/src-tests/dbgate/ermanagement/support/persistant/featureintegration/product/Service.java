package dbgate.ermanagement.support.persistant.featureintegration.product;

import dbgate.DBColumnType;
import dbgate.ermanagement.DBColumnInfo;
import dbgate.ermanagement.DBTableInfo;

/**
 * Date: Mar 31, 2011
 * Time: 9:46:42 PM
 */
@DBTableInfo(tableName = "product_service")
public class Service extends Item
{
    @DBColumnInfo(columnType = DBColumnType.DOUBLE)
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
