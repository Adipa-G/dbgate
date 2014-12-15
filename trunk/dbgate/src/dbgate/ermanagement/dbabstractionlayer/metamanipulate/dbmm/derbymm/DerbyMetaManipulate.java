package dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.derbymm;

import dbgate.ColumnType;
import dbgate.ITransaction;
import dbgate.ReferentialRuleType;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonColumnGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonForeignKeyGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonPrimaryKeyGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.MetaComparisonTableGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.*;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.dbmm.defaultmm.DefaultMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.mappings.ReferentialRuleTypeMapItem;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 30, 2010
 * Time: 8:14:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DerbyMetaManipulate extends DefaultMetaManipulate
{
    public DerbyMetaManipulate(IDBLayer dbLayer)
    {
        super(dbLayer);
    }

	@Override
	protected String createCreateForeignKeyQuery(MetaComparisonTableGroup tableGroup,
	                                             MetaComparisonForeignKeyGroup foreignKeyGroup)
	{
		//no foreign keys supported for derby for the moment
		//because it will fail to create a key if the target table's column(s) are not
		//either unique or primary key
		return null;
	}
}
