package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate;

import dbgate.ColumnType;
import dbgate.ermanagement.ReferentialRuleType;
import dbgate.ermanagement.exceptions.migration.MetaDataException;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.compare.IMetaComparisonGroup;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.support.MetaQueryHolder;

import java.sql.Connection;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 6:51:41 PM
 */
public interface IMetaManipulate
{
    void initialize(Connection con) throws MetaDataException;

    ColumnType mapColumnTypeNameToType(String columnTypeName);

    String mapColumnTypeToTypeName(ColumnType columnTypeId);

    String getDefaultValueForType(ColumnType columnTypeId);

    ReferentialRuleType mapReferentialRuleNameToType(String ruleTypeName);

    Collection<IMetaItem> getMetaData(Connection con) throws MetaDataException;

    Collection<MetaQueryHolder> createDbPathSQL(IMetaComparisonGroup metaComparisonGroup);

    boolean Equals(IMetaItem iMetaItemA, IMetaItem iMetaItemB);
}
