package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate;

import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.MetaItemType;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.MetaPrimaryKey;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 30, 2010
 * Time: 8:14:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySqlMetaManipulate extends DefaultMetaManipulate
{
    public MySqlMetaManipulate(IDBLayer dbLayer)
    {
        super(dbLayer);
    }

    @Override
    public boolean Equals(IMetaItem iMetaItemA, IMetaItem iMetaItemB)
    {
        if (iMetaItemA.getItemType() == MetaItemType.PRIMARY_KEY
                && iMetaItemA.getItemType() == iMetaItemB.getItemType())
        {
            MetaPrimaryKey primaryKeyA = (MetaPrimaryKey) iMetaItemA;
            MetaPrimaryKey primaryKeyB = (MetaPrimaryKey) iMetaItemB;

            if (primaryKeyA.getColumnNames().size() != primaryKeyB.getColumnNames().size())
            {
                return false;
            }
            for (String columnA : primaryKeyA.getColumnNames())
            {
                boolean found = false;
                for (String columnB : primaryKeyB.getColumnNames())
                {
                    if (columnA.equalsIgnoreCase(columnB))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    return false;
                }
            }
            return true;
        }
        return super.Equals(iMetaItemA, iMetaItemB);
    }
}
