package dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 3:18:10 PM
 */
public class MetaPrimaryKey extends AbstractMetaItem
{
    private Collection<String> columnNames;

    public MetaPrimaryKey()
    {
        itemType = MetaItemType.PRIMARY_KEY;
        columnNames = new ArrayList<String>();
    }

    public Collection<String> getColumnNames()
    {
        return columnNames;
    }

    public void setColumnNames(Collection<String> columnNames)
    {
        this.columnNames = columnNames;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

	    if (this == o) return true;
	    if (!(o instanceof AbstractMetaItem)) return false;

	    MetaPrimaryKey that = (MetaPrimaryKey) o;

	    if (itemType != that.itemType) return false;
	    if (!"PRIMARY".equalsIgnoreCase(name)
	        && !"PRIMARY".equalsIgnoreCase(that.getName())
	        && !name.equalsIgnoreCase(that.name)) return false;

        boolean foundMatch = false;
        for (String thisColumn : columnNames)
        {
            for (String thatColumn : that.getColumnNames())
            {
                if (thisColumn.equalsIgnoreCase(thatColumn))
                {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch)
            {
                return false;
            }
        }

        return true;
    }
}
