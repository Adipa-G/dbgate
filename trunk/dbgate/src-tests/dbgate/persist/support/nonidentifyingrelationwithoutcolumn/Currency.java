package dbgate.persist.support.nonidentifyingrelationwithoutcolumn;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.DefaultEntity;
import dbgate.TableInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/2/12
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
@TableInfo(tableName = "relation_test_currency")
public class Currency extends DefaultEntity
{
    @ColumnInfo(columnType = ColumnType.INTEGER,key = true)
    private int currencyId;
    @ColumnInfo(columnType = ColumnType.VARCHAR)
    private String code;

    public int getCurrencyId()
    {
        return currencyId;
    }

    public void setCurrencyId(int currencyId)
    {
        this.currencyId = currencyId;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
}

