package dbgate.ermanagement.impl;

import dbgate.ServerDBClass;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.DBPatchingException;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.SequenceGeneratorInitializationException;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.IMetaManipulate;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.compare.CompareUtility;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.compare.IMetaComparisonGroup;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.datastructures.*;
import dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.support.MetaQueryHolder;
import dbgate.ermanagement.impl.utils.DBClassAttributeExtractionUtils;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.impl.utils.ReflectionUtils;

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
public class ERMetaDataManager
{
    private IDBLayer dbLayer;
    private IERLayerConfig config;

    public ERMetaDataManager(IDBLayer dbLayer,IERLayerConfig config)
    {
        this.dbLayer = dbLayer;
        this.config = config;
    }

    public void patchDataBase(Connection con, Collection<ServerDBClass> dbClasses,boolean dropAll) throws DBPatchingException
    {
        try
        {
            for (ServerDBClass dbClass : dbClasses)
            {
                Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(dbClass.getClass(),new Class[]{ServerDBClass.class});
                for (Class type : typeList)
                {
                    CacheManager.tableCache.register(type,dbClass);
                    CacheManager.fieldCache.register(type,dbClass);
                }
            }
            
            IMetaManipulate metaManipulate =  dbLayer.getMetaManipulate(con);
            Collection<IMetaItem> existingItems = metaManipulate.getMetaData(con);
            Collection<IMetaItem> requiredItems = createMetaItemsFromDbClasses(dbClasses);

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
                Logger.getLogger(config.getLoggerName()).info(holder.getQueryString());

                PreparedStatement ps = con.prepareStatement(holder.getQueryString());
                ps.execute();
                DBMgmtUtility.close(ps);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE,e.getMessage(),e);
            throw new DBPatchingException(e.getMessage(),e);
        }
    }

    private Collection<IMetaItem> createMetaItemsFromDbClasses(Collection<ServerDBClass> dbClasses) throws SequenceGeneratorInitializationException
            , TableCacheMissException, FieldCacheMissException
    {
        Collection<IMetaItem> metaItems = new ArrayList<IMetaItem>();
        Collection<String> uniqueNames = new ArrayList<String>();

        for (ServerDBClass serverDBClass : dbClasses)
        {
            Collection<IMetaItem> classMetaItems =extractMetaItems(serverDBClass);
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

    private Collection<IMetaItem> extractMetaItems(ServerDBClass serverDBClass) throws SequenceGeneratorInitializationException
            , TableCacheMissException, FieldCacheMissException
    {
        Collection<IMetaItem> retItems = new ArrayList<IMetaItem>();

        Class[] superTypes = ReflectionUtils.getSuperTypesWithInterfacesImplemented(serverDBClass.getClass(),new Class[]{ServerDBClass.class});
        for (Class superType : superTypes)
        {
            String tableName = DBClassAttributeExtractionUtils.getTableName(serverDBClass,superType);
            if (tableName == null)
            {
                continue;
            }
            Collection<IField> fields = DBClassAttributeExtractionUtils.getAllFields(serverDBClass,superType);

            Collection<IDBColumn> dbColumns = new ArrayList<IDBColumn>();
            Collection<IDBRelation> dbRelations = new ArrayList<IDBRelation>();

            for (IField field : fields)
            {
                if (field instanceof IDBColumn)
                {
                    dbColumns.add((IDBColumn) field);
                }
                else if (field instanceof IDBRelation)
                {
                    IDBRelation relation = (IDBRelation)field;
                    if (!relation.isReverseRelationship()
                            && !relation.isNonIdentifyingRelation())
                    {
                        dbRelations.add(relation);
                    }
                }
            }

            retItems.add(createTable(superType,dbColumns,dbRelations));
        }
        return retItems;
    }

    private IMetaItem createTable(Class type,Collection<IDBColumn> dbColumns,Collection<IDBRelation> dbRelations) throws TableCacheMissException
            , FieldCacheMissException
    {
        MetaTable table = new MetaTable();
        table.setName(CacheManager.tableCache.getTableName(type));

        for (IDBColumn dbColumn : dbColumns)
        {
            MetaColumn metaColumn = new MetaColumn();
            metaColumn.setColumnType(dbColumn.getColumnType());
            metaColumn.setName(dbColumn.getColumnName());
            metaColumn.setNull(dbColumn.isNullable());
            metaColumn.setSize(dbColumn.getSize());
            table.getColumns().add(metaColumn);
        }

        for (IDBRelation relation : dbRelations)
        {
            MetaForeignKey foreignKey = new MetaForeignKey();
            foreignKey.setName(relation.getRelationshipName());
            foreignKey.setToTable(CacheManager.tableCache.getTableName(relation.getRelatedObjectType()));
            for (DBRelationColumnMapping mapping : relation.getTableColumnMappings())
            {
                String fromCol = ERDataManagerUtils.findColumnByAttribute(CacheManager.fieldCache.getColumns(type),mapping.getFromField()).getColumnName();
                String toCol = ERDataManagerUtils.findColumnByAttribute(CacheManager.fieldCache.getColumns(relation.getRelatedObjectType()),mapping.getToField()).getColumnName();
                foreignKey.getColumnMappings().add(new MetaForeignKeyColumnMapping(fromCol,toCol));
            }
            foreignKey.setDeleteRule(relation.getDeleteRule());
            foreignKey.setUpdateRule(relation.getUpdateRule());
            table.getForeignKeys().add(foreignKey);
        }

        MetaPrimaryKey primaryKey = new MetaPrimaryKey();
        primaryKey.setName("pk_" + table.getName());
        for (IDBColumn dbColumn : dbColumns)
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
