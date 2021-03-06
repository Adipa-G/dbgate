package dbgate.ermanagement.dbabstractionlayer.metamanipulate;

import dbgate.ColumnType;
import dbgate.ITransaction;
import dbgate.ReferentialRuleType;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.IMetaComparisonGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.support.MetaQueryHolder;
import dbgate.exceptions.migration.MetaDataException;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 6:51:41 PM
 */
public interface IMetaManipulate
{
    void initialize(ITransaction tx) throws MetaDataException;

    ColumnType mapColumnTypeNameToType(String columnTypeName);

    String mapColumnTypeToTypeName(ColumnType columnTypeId);

    String getDefaultValueForType(ColumnType columnTypeId);

    ReferentialRuleType mapReferentialRuleNameToType(String ruleTypeName);

    Collection<IMetaItem> getMetaData(ITransaction tx) throws MetaDataException;

    Collection<MetaQueryHolder> createDbPathSQL(IMetaComparisonGroup metaComparisonGroup);

    boolean Equals(IMetaItem iMetaItemA, IMetaItem iMetaItemB);
}
