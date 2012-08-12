package dbgate.ermanagement.dbabstractionlayer.metamanipulate.support;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 8:07:05 PM
 */
public class MetaQueryHolder implements Comparable<MetaQueryHolder>
{
    public static final int OBJECT_TYPE_TABLE = 1;
    public static final int OBJECT_TYPE_COLUMN = 2;
    public static final int OBJECT_TYPE_PRIMARY_KEY = 3;
    public static final int OBJECT_TYPE_FOREIGN_KEY = 4;

    public static final int OPERATION_TYPE_ADD = 1;
    public static final int OPERATION_TYPE_ALTER = 2;
    public static final int OPERATION_TYPE_DELETE = 3;

    private int itemType;
    private int queryType;
    private String queryString;

    public MetaQueryHolder()
    {
    }

    public MetaQueryHolder(int itemType, int queryType, String queryString)
    {
        this.itemType = itemType;
        this.queryType = queryType;
        this.queryString = queryString;
    }

    public int getItemType()
    {
        return itemType;
    }

    public void setItemType(int itemType)
    {
        this.itemType = itemType;
    }

    public int getQueryType()
    {
        return queryType;
    }

    public void setQueryType(int queryType)
    {
        this.queryType = queryType;
    }

    public String getQueryString()
    {
        return queryString;
    }

    public void setQueryString(String queryString)
    {
        this.queryString = queryString;
    }

    public int compareTo(MetaQueryHolder metaQueryHolder)
    {
        if (queryType != metaQueryHolder.getQueryType())
        {
            return new Integer(this.queryType).compareTo(metaQueryHolder.getQueryType());
        }
        else
        {
            if (queryType == OPERATION_TYPE_DELETE)
            {
                return -1 * new Integer(this.itemType).compareTo(metaQueryHolder.getItemType());
            }
            else
            {
                return new Integer(this.itemType).compareTo(metaQueryHolder.getItemType());
            }
        }
    }
}
