package dbgate.ermanagement.ermapper;

import dbgate.*;
import dbgate.utility.DBMgtUtility;
import dbgate.caches.CacheManager;
import dbgate.caches.impl.EntityInfo;
import dbgate.exceptions.DBPatchingException;
import dbgate.exceptions.SequenceGeneratorInitializationException;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.IMetaManipulate;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.CompareUtility;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.compare.IMetaComparisonGroup;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.datastructures.*;
import dbgate.ermanagement.dbabstractionlayer.metamanipulate.support.MetaQueryHolder;
import dbgate.ermanagement.ermapper.utils.OperationUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 11:03:02 PM
 */
public class DataMigrationLayer
{
    private IDBLayer dbLayer;
    private IDbGateStatistics statistics;
    private IDbGateConfig config;

    public DataMigrationLayer(IDBLayer dbLayer, IDbGateStatistics statistics, IDbGateConfig config)
    {
        this.dbLayer = dbLayer;
        this.statistics = statistics;
        this.config = config;
    }

    public void patchDataBase(Connection con, Collection<Class> entityTypes,boolean dropAll) throws DBPatchingException
    {
        try
        {
            for (Class type : entityTypes)
            {
                CacheManager.register(type);
            }
            
            IMetaManipulate metaManipulate =  dbLayer.getMetaManipulate(con);
            Collection<IMetaItem> existingItems = metaManipulate.getMetaData(con);
            Collection<IMetaItem> requiredItems = createMetaItemsFromDbClasses(entityTypes);

            ArrayList<MetaQueryHolder> queryHolders = new ArrayList<MetaQueryHolder>();

            if (dropAll)
            {
                Collection<IMetaComparisonGroup> groupExisting = CompareUtility.compare(metaManipulate,existingItems,new ArrayList<IMetaItem>());
                Collection<IMetaComparisonGroup> groupRequired = CompareUtility.compare(metaManipulate,new ArrayList<IMetaItem>(), requiredItems);

                ArrayList<MetaQueryHolder> queryHoldersExisting = new ArrayList<MetaQueryHolder>();
                for (IMetaComparisonGroup comparisonGroup : groupExisting)
                {
                    queryHoldersExisting.addAll(dbLayer.getMetaManipulate(con).createDbPathSQL(comparisonGroup));
                }
                Collections.sort(queryHoldersExisting);

                ArrayList<MetaQueryHolder> queryHoldersRequired = new ArrayList<MetaQueryHolder>();
                for (IMetaComparisonGroup comparisonGroup : groupRequired)
                {
                    queryHoldersRequired.addAll(dbLayer.getMetaManipulate(con).createDbPathSQL(comparisonGroup));
                }
                Collections.sort(queryHoldersRequired);

                queryHolders.addAll(queryHoldersExisting);
                queryHolders.addAll(queryHoldersRequired);    
            }
            else
            {
                Collection<IMetaComparisonGroup> groups = CompareUtility.compare(metaManipulate,existingItems,requiredItems);
                for (IMetaComparisonGroup comparisonGroup : groups)
                {
                    queryHolders.addAll(dbLayer.getMetaManipulate(con).createDbPathSQL(comparisonGroup));
                }
                Collections.sort(queryHolders);
            }

            for (MetaQueryHolder holder : queryHolders)
            {
                if (holder.getQueryString() == null)
                    continue;

                Logger.getLogger(config.getLoggerName()).info(holder.getQueryString());

                PreparedStatement ps = con.prepareStatement(holder.getQueryString());
                ps.execute();
                DBMgtUtility.close(ps);

                if (config.isEnableStatistics()) statistics.registerPatch();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE,e.getMessage(),e);
            throw new DBPatchingException(e.getMessage(),e);
        }
    }

    private Collection<IMetaItem> createMetaItemsFromDbClasses(Collection<Class> entityTypes)
            throws SequenceGeneratorInitializationException
    {
        Collection<IMetaItem> metaItems = new ArrayList<IMetaItem>();
        Collection<String> uniqueNames = new ArrayList<String>();

        for (Class type : entityTypes)
        {
            Collection<IMetaItem> classMetaItems =extractMetaItems(type);
            for (IMetaItem metaItem : classMetaItems)
            {
                //this is to remove duplicate tables in case of different sub classes inheriting same superclass
                if (!uniqueNames.contains(metaItem.getName()))
                {
                    metaItems.add(metaItem);
                    uniqueNames.add(metaItem.getName());
                }
            }
        }
        return metaItems;
    }

    private Collection<IMetaItem> extractMetaItems(Class subType)
            throws SequenceGeneratorInitializationException
    {
        Collection<IMetaItem> retItems = new ArrayList<IMetaItem>();
        EntityInfo entityInfo = CacheManager.getEntityInfo(subType);
        while (entityInfo != null)
        {
            Collection<IColumn> dbColumns = entityInfo.getColumns();
            Collection<IRelation> dbRelations = entityInfo.getRelations();
            Collection<IRelation> filteredRelations = new ArrayList<>();

            for (IRelation relation : dbRelations)
            {
                if (!relation.isReverseRelationship()
                        && !relation.isNonIdentifyingRelation())
                {
                    filteredRelations.add(relation);
                }
            }
            retItems.add(createTable(entityInfo.getEntityType(),dbColumns,filteredRelations));

            entityInfo = entityInfo.getSuperEntityInfo();
        }
        return retItems;
    }

    private IMetaItem createTable(Class type,Collection<IColumn> dbColumns,Collection<IRelation> dbRelations)
    {
        MetaTable table = new MetaTable();
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        table.setName(entityInfo.getTableName());

        for (IColumn dbColumn : dbColumns)
        {
            MetaColumn metaColumn = new MetaColumn();
            metaColumn.setColumnType(dbColumn.getColumnType());
            metaColumn.setName(dbColumn.getColumnName());
            metaColumn.setNull(dbColumn.isNullable());
            metaColumn.setSize(dbColumn.getSize());
            table.getColumns().add(metaColumn);
        }

        for (IRelation relation : dbRelations)
        {
            EntityInfo relatedEntityInfo = CacheManager.getEntityInfo(relation.getRelatedObjectType());

            MetaForeignKey foreignKey = new MetaForeignKey();
            foreignKey.setName(relation.getRelationshipName());
            foreignKey.setToTable(relatedEntityInfo.getTableName());
            for (RelationColumnMapping mapping : relation.getTableColumnMappings())
            {
                String fromCol = OperationUtils.findColumnByAttribute(entityInfo.getColumns(), mapping.getFromField()).getColumnName();
                String toCol = OperationUtils.findColumnByAttribute(relatedEntityInfo.getColumns(),
                                                                    mapping.getToField()).getColumnName();
                foreignKey.getColumnMappings().add(new MetaForeignKeyColumnMapping(fromCol,toCol));
            }
            foreignKey.setDeleteRule(relation.getDeleteRule());
            foreignKey.setUpdateRule(relation.getUpdateRule());
            table.getForeignKeys().add(foreignKey);
        }

        MetaPrimaryKey primaryKey = new MetaPrimaryKey();
        primaryKey.setName("pk_" + table.getName());
        for (IColumn dbColumn : dbColumns)
        {
            if (dbColumn.isKey())
            {
                primaryKey.getColumnNames().add(dbColumn.getColumnName());
            }
        }
        if (primaryKey.getColumnNames().size() > 0)
        {
            table.setPrimaryKey(primaryKey);
        }

        return table;
    }
}
