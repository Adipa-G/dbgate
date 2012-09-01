package dbgate.complexexample.entities.product;

import dbgate.ColumnInfo;
import dbgate.ColumnType;
import dbgate.DefaultEntity;
import dbgate.TableInfo;
import docgenerate.WikiCodeBlock;

/**
 * Date: Mar 31, 2011
 * Time: 9:45:55 PM
 */
@WikiCodeBlock(id = "complex_example_product_item")
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
