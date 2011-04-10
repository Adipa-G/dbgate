package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.compare;

import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.IMetaManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.IMetaItem;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.MetaItemType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 3, 2010
 * Time: 6:43:03 PM
 */
public class CompareUtility
{
    public static Collection<IMetaComparisonGroup> compare(IMetaManipulate metaManipulate,Collection<IMetaItem> existing,Collection<IMetaItem> required)
    {
        Collection<IMetaComparisonGroup> retGroups = new ArrayList<IMetaComparisonGroup>();

        for (IMetaItem existingItem : existing)
        {
            boolean found = false;
            for (IMetaItem requiredItem : required)
            {
                if (metaManipulate.Equals(existingItem,requiredItem))
                {
                    retGroups.add(createGroupByType(metaManipulate,existingItem.getItemType(),existingItem,requiredItem));
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                retGroups.add(createGroupByType(metaManipulate,existingItem.getItemType(),existingItem,null));
            }
        }

        for (IMetaItem requiredItem : required)
        {
            boolean found = false;
            for (IMetaItem existingItem : existing)
            {
                if (metaManipulate.Equals(requiredItem,existingItem))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                retGroups.add(createGroupByType(metaManipulate,requiredItem.getItemType(),null,requiredItem));
            }
        }

        return retGroups;
    }

    private static IMetaComparisonGroup createGroupByType(IMetaManipulate metaManipulate,MetaItemType type,IMetaItem existingItem,IMetaItem requiredItem)
    {
        switch (type)
        {
            case COLUMN:
                return new MetaComparisonColumnGroup(existingItem,requiredItem);
            case FOREIGN_KEY:
                return new MetaComparisonForeignKeyGroup(existingItem,requiredItem);
            case PRIMARY_KEY:
                return new MetaComparisonPrimaryKeyGroup(existingItem,requiredItem);
            case TABLE:
                MetaComparisonTableGroup group = new MetaComparisonTableGroup(existingItem,requiredItem);
                processTableSubGroups(metaManipulate,group);
                return group;
            default:
                return null;
        }
    }

    private static void processTableSubGroups(IMetaManipulate metaManipulate,MetaComparisonTableGroup tableGroup)
    {
        if (tableGroup._shouldDeleteFromDB())
        {
            return;
        }
        //columns
        if (tableGroup._shouldAlterInDB())
        {
            Collection<IMetaItem> existingColumns = new ArrayList<IMetaItem>();
            Collection<IMetaItem> requiredColumns = new ArrayList<IMetaItem>();
            Collection<MetaComparisonColumnGroup> comparedColumns = new ArrayList<MetaComparisonColumnGroup>();
            
            existingColumns.addAll(tableGroup.getExistingItem().getColumns());
            requiredColumns.addAll(tableGroup.getRequiredItem().getColumns());
            
            Collection<IMetaComparisonGroup> compared = compare(metaManipulate,existingColumns,requiredColumns);
            for (IMetaComparisonGroup columnComparison : compared)
            {
                comparedColumns.add((MetaComparisonColumnGroup) columnComparison);
            }
            tableGroup.setColumns(comparedColumns);
        }
        
        //fkeys
        {
            Collection<IMetaItem> existingForeignKeys = new ArrayList<IMetaItem>();
            Collection<IMetaItem> requiredForeignKeys = new ArrayList<IMetaItem>();
            Collection<MetaComparisonForeignKeyGroup> comparedForeignKeys = new ArrayList<MetaComparisonForeignKeyGroup>();

            if (tableGroup.getExistingItem() != null)
            {
                existingForeignKeys.addAll(tableGroup.getExistingItem().getForeignKeys());
            }
            requiredForeignKeys.addAll(tableGroup.getRequiredItem().getForeignKeys());
            
            Collection<IMetaComparisonGroup> compared = compare(metaManipulate,existingForeignKeys,requiredForeignKeys);
            for (IMetaComparisonGroup columnComparison : compared)
            {
                comparedForeignKeys.add((MetaComparisonForeignKeyGroup) columnComparison);
            }
            tableGroup.setForeignKeys(comparedForeignKeys);
        }
        
        //pk
        {
            Collection<IMetaItem> existingPrimaryKey = new ArrayList<IMetaItem>();
            Collection<IMetaItem> requiredPrimaryKey = new ArrayList<IMetaItem>();
            Collection<MetaComparisonPrimaryKeyGroup> comparedPrimaryKey = new ArrayList<MetaComparisonPrimaryKeyGroup>();

            if (tableGroup.getExistingItem() != null
                    && tableGroup.getExistingItem().getPrimaryKey() != null)
            {
                existingPrimaryKey.add(tableGroup.getExistingItem().getPrimaryKey());
            }
            if (tableGroup.getRequiredItem() != null
                    && tableGroup.getRequiredItem().getPrimaryKey() != null)
            {
                requiredPrimaryKey.add(tableGroup.getRequiredItem().getPrimaryKey());
            }
            
            Collection<IMetaComparisonGroup> compared = compare(metaManipulate,existingPrimaryKey,requiredPrimaryKey);
            for (IMetaComparisonGroup columnComparison : compared)
            {
                comparedPrimaryKey.add((MetaComparisonPrimaryKeyGroup) columnComparison);
            }
            if (comparedPrimaryKey.size() > 0)
            {
                tableGroup.setPrimaryKey(comparedPrimaryKey.iterator().next());
            }
        }
    }
}
