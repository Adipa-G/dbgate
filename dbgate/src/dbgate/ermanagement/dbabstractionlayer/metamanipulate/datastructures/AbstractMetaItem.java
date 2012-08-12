package dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 3:21:39 PM
 */
public abstract class AbstractMetaItem implements IMetaItem
{
    protected String name;
    protected MetaItemType itemType;

    public AbstractMetaItem()
    {
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public MetaItemType getItemType()
    {
        return itemType;
    }

    public void setItemType(MetaItemType itemType)
    {
        this.itemType = itemType;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AbstractMetaItem)) return false;

        AbstractMetaItem that = (AbstractMetaItem) o;

        if (itemType != that.itemType) return false;
        if (!name.equalsIgnoreCase(that.name)) return false;

        return true;
    }
}
