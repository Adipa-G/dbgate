package dbgate.caches.impl;

import dbgate.*;
import dbgate.caches.IEntityInfoCache;
import dbgate.ermanagement.ermapper.utils.ReflectionUtils;
import dbgate.exceptions.SequenceGeneratorInitializationException;
import dbgate.exceptions.common.EntityRegistrationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityInfoCache implements IEntityInfoCache
{
    private final static ConcurrentHashMap<Class,EntityInfo> cache = new ConcurrentHashMap <>();
    private IDbGateConfig config;

    public EntityInfoCache(IDbGateConfig config)
    {
        this.config = config;
    }

    @Override
    public EntityInfo getEntityInfo(Class type)
    {
        try
        {
            if (!cache.containsKey(type))
            {
                synchronized (cache)
                {
                    if (!cache.containsKey(type))
                    {
                        register(type);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE,ex.getMessage(),ex);
        }
        return cache.get(type);
    }

    @Override
    public EntityInfo getEntityInfo(IReadOnlyClientEntity entity)
    {
        Class type = entity.getClass();
        return getEntityInfo(type);
    }

    @Override
    public Collection<IRelation> GetReversedRelationships(Class entityType)
    {
        Collection<IRelation> results = new ArrayList<>();

        Class[] typeArray = ReflectionUtils.getSuperTypesWithInterfacesImplemented(
                entityType,new Class[]{IReadOnlyEntity.class});
        List<Class> typeList = Arrays.asList(typeArray);

        for (EntityInfo entityInfo : cache.values()) {
            for (IRelation relation : entityInfo.getRelations()) {
                if (!relation.isReverseRelationship() &&
                    !relation.isNonIdentifyingRelation() &&
                    typeList.contains(relation.getRelatedObjectType())){

                    results.add(relation);
                }
            }
        }

        return results;
    }

    @Override
    public void register(Class type, ITable tableInfo, Collection<IField> fields)
    {
        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(type,new Class[]{IReadOnlyEntity.class});
        Class immediateSuper = typeList.length > 1 ? typeList[1] : null;

        EntityInfo subEntityInfo = new EntityInfo(type);
        subEntityInfo.setFields(fields);
        subEntityInfo.setTableInfo(tableInfo);

        if (immediateSuper != null
                && cache.containsKey(immediateSuper))
        {
            EntityInfo immediateSuperEntityInfo = cache.get(immediateSuper);
            subEntityInfo.setSuperEntityInfo(immediateSuperEntityInfo);
            immediateSuperEntityInfo.addSubEntityInfo(subEntityInfo);
        }

        cache.put(type,subEntityInfo);
    }

    public void register(Class type) throws SequenceGeneratorInitializationException, EntityRegistrationException
    {
        if (cache.containsKey(type))
        {
            return;
        }
        HashMap<Class,EntityInfo> extracted = extractTableAndFieldInfo(type);

        for (Class regType : extracted.keySet())
        {
            cache.put(regType,extracted.get(regType));
        }
    }

    @Override
    public void clear()
    {
        synchronized (cache)
        {
            cache.clear();
        }
    }

    private HashMap<Class,EntityInfo> extractTableAndFieldInfo(Class subType) throws SequenceGeneratorInitializationException,EntityRegistrationException
    {
        HashMap<Class,EntityInfo> entityInfoMap = new HashMap<>();

        EntityInfo subEntity = null;
        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(subType,new Class[]{IReadOnlyEntity.class});
        for (Class regType : typeList)
        {
            if (subEntity != null && cache.containsKey(regType))
            {
                EntityInfo superEntityInfo = cache.get(regType);
                subEntity.setSuperEntityInfo(superEntityInfo);
                superEntityInfo.addSubEntityInfo(subEntity);
                continue;
            }

            ITable tableInfo = getTableInfo(regType, subType);
            Collection<IField> fields = getAllFields(regType, subType);

            if (tableInfo != null || fields.size() > 0)
            {
                EntityInfo entityInfo = new EntityInfo(regType);
                entityInfo.setFields(fields);
                entityInfo.setTableInfo(tableInfo);
                if (subEntity != null)
                {
                    subEntity.setSuperEntityInfo(entityInfo);
                    entityInfo.addSubEntityInfo(subEntity);
                }
                entityInfoMap.put(regType,entityInfo);
                subEntity = entityInfo;
            }
        }
        return entityInfoMap;
    }

    private ITable getTableInfo(Class regType, Class subType) throws EntityRegistrationException
    {
        ITable tableInfo = getTableInfoIfManagedClass(regType, subType);
        if (tableInfo == null)
        {
            Annotation[] annotations = regType.getAnnotations();
            for (Annotation annotation : annotations)
            {
                if (annotation instanceof TableInfo)
                {
                    TableInfo annotatedTableInfo = (TableInfo) annotation;
                    tableInfo = new DefaultTable(annotatedTableInfo.tableName()
                            ,annotatedTableInfo.updateStrategy()
                            ,annotatedTableInfo.verifyOnWriteStrategy()
                            ,annotatedTableInfo.dirtyCheckStrategy());
                    break;
                }
            }
        }
        updateToDefaultStrategiesIfNoneDefined(tableInfo);
        return tableInfo;
    }

    private void updateToDefaultStrategiesIfNoneDefined(ITable tableInfo)
    {
        if (tableInfo != null)
        {
            if (tableInfo.getDirtyCheckStrategy() == DirtyCheckStrategy.DEFAULT)
                tableInfo.setDirtyCheckStrategy(config.getDefaultDirtyCheckStrategy());

            if (tableInfo.getUpdateStrategy() == UpdateStrategy.DEFAULT)
                tableInfo.setUpdateStrategy(config.getDefaultUpdateStrategy());

            if (tableInfo.getVerifyOnWriteStrategy() == VerifyOnWriteStrategy.DEFAULT)
                tableInfo.setVerifyOnWriteStrategy(config.getDefaultVerifyOnWriteStrategy());
        }
    }

    private static ITable getTableInfoIfManagedClass(Class regType, Class subType) throws EntityRegistrationException
    {
        if (ReflectionUtils.isImplementInterface(regType, IManagedEntity.class))
        {
            try
            {
                IManagedEntity managedEntity = (IManagedEntity)subType.newInstance();
                return managedEntity.getTableInfo().get(regType);
            }
            catch (Exception e)
            {
                throw  new EntityRegistrationException(String.format("Could not register type %s",regType.getCanonicalName()),e);
            }
        }
        return null;
    }

    private Collection<IField> getAllFields(Class regType,Class subType) throws EntityRegistrationException,SequenceGeneratorInitializationException
    {
        Collection<IField> fields = getFieldsIfManagedClass(regType,subType);
        Class[] superTypes = ReflectionUtils.getSuperTypesWithInterfacesImplemented(regType,new Class[]{IReadOnlyEntity.class});

        for (int i = 0; i < superTypes.length; i++)
        {
            Class superType = superTypes[i];
            fields.addAll(getAllFields(superType,i > 0));
        }

        for (IField field : fields)
        {
            if (field instanceof IRelation)
            {
                IRelation relation = (IRelation) field;

                if (relation.getFetchStrategy() == FetchStrategy.DEFAULT)
                    relation.setFetchStrategy(config.getDefaultFetchStrategy());
            }
        }

        return fields;
    }

    private static Collection<IField> getFieldsIfManagedClass(Class regType,Class subType) throws EntityRegistrationException
    {
        if (ReflectionUtils.isImplementInterface(regType, IManagedEntity.class))
        {
            try
            {
                IManagedEntity managedEntity = (IManagedEntity)subType.newInstance();
                return getFieldsForManagedClass(managedEntity,regType);
            }
            catch (Exception e)
            {
                throw  new EntityRegistrationException(String.format("Could not register type %s",regType.getCanonicalName()),e);
            }
        }
        return new ArrayList<>();
    }

    private static Collection<IField> getFieldsForManagedClass(IManagedReadOnlyEntity entity,Class type)
    {
        Collection<IField> fields = new ArrayList<>();

        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(type,new Class[]{IReadOnlyEntity.class});
        for (Class targetType : typeList)
        {
            Collection<IField> targetTypeFields = entity.getFieldInfo().get(targetType);
            if (targetType == type && targetTypeFields != null)
            {
                fields.addAll(targetTypeFields);
            }
            else if (targetTypeFields != null)
            {
                for (IField field : targetTypeFields)
                {
                    if (field instanceof IColumn)
                    {
                        IColumn column = (IColumn) field;
                        if (column.isSubClassCommonColumn())
                        {
                            fields.add(column);
                        }
                    }
                }
            }
        }

        return fields;
    }

    private Collection<IField> getAllFields(Class type,boolean superClass) throws SequenceGeneratorInitializationException
    {
        Collection<IField> fields = new ArrayList<IField>();

        Field[] dbClassFields = type.getDeclaredFields();
        for (Field dbClassField : dbClassFields)
        {
            Annotation[] annotations = dbClassField.getAnnotations();
            for (Annotation annotation : annotations)
            {
                if (annotation instanceof ColumnInfo)
                {
                    ColumnInfo columnInfo = (ColumnInfo) annotation;
                    if (superClass && !columnInfo.subClassCommonColumn())
                    {
                        continue;
                    }
                    IColumn column = createColumnMapping(dbClassField, columnInfo);
                    fields.add(column);
                }
                else
                {
                    if (superClass) continue;
                    if (annotation instanceof ForeignKeyInfo)
                    {
                        ForeignKeyInfo foreignKeyInfo = (ForeignKeyInfo) annotation;
                        IRelation relation = createForeignKeyMapping(type,dbClassField, foreignKeyInfo);
                        fields.add(relation);
                    }
                    if (annotation instanceof ForeignKeyInfoList)
                    {
                        ForeignKeyInfoList foreignKeyInfoList = (ForeignKeyInfoList) annotation;
                        for (ForeignKeyInfo foreignKeyInfo : foreignKeyInfoList.infoList())
                        {
                            IRelation relation = createForeignKeyMapping(type,dbClassField, foreignKeyInfo);
                            fields.add(relation);
                        }
                    }
                }
            }
        }

        return fields;
    }

    private static IColumn createColumnMapping(Field dbClassField, ColumnInfo columnInfo) throws SequenceGeneratorInitializationException
    {
        IColumn column = new DefaultColumn(dbClassField.getName(), columnInfo.columnType(), columnInfo.nullable());
        if (columnInfo.columnName() != null
                && columnInfo.columnName().trim().length() > 0)
        {
            column.setColumnName(columnInfo.columnName());
        }
        column.setKey(columnInfo.key());
        column.setSize(columnInfo.size());
        column.setSubClassCommonColumn(columnInfo.subClassCommonColumn());
        column.setReadFromSequence(columnInfo.readFromSequence());
        column.setSequenceGenerator(createSequenceGenerator(columnInfo, column));
        return column;
    }

    private static ISequenceGenerator createSequenceGenerator(ColumnInfo columnInfo, IColumn column)
            throws SequenceGeneratorInitializationException
    {
        if (column.isReadFromSequence())
        {
            try
            {
                Class generatorClass = Class.forName(columnInfo.sequenceGeneratorClassName());
                return (ISequenceGenerator)ReflectionUtils.createInstance(generatorClass);
            }
            catch (Exception e)
            {
                throw new SequenceGeneratorInitializationException(String.format("Could not initialize sequence generator %s",
                                                                                 columnInfo.sequenceGeneratorClassName()),e);
            }
        }
        return null;
    }

    private IRelation createForeignKeyMapping(Class entityType,Field dbClassField, ForeignKeyInfo foreignKeyInfo)
    {
        RelationColumnMapping[] columnMappings = createForeignKeyColumnMappings(foreignKeyInfo);

        return new DefaultRelation(dbClassField.getName(),
                                   foreignKeyInfo.name(),
                                   entityType,
                                   foreignKeyInfo.relatedObjectType(),
                                   columnMappings,
                                   foreignKeyInfo.updateRule(),
                                   foreignKeyInfo.deleteRule(),
                                   foreignKeyInfo.reverseRelation(),
                                   foreignKeyInfo.nonIdentifyingRelation(),
                                   foreignKeyInfo.fetchStrategy(),
                                   foreignKeyInfo.nullable());
    }

    private RelationColumnMapping[] createForeignKeyColumnMappings(ForeignKeyInfo foreignKeyInfo)
    {
        RelationColumnMapping[] columnMappings = new RelationColumnMapping[foreignKeyInfo.fieldMappings().length];
        ForeignKeyFieldMapping[] annotationMappings = foreignKeyInfo.fieldMappings();
        for (int i = 0, columnMappingsLength = annotationMappings.length; i < columnMappingsLength; i++)
        {
            ForeignKeyFieldMapping mapping = annotationMappings[i];
            columnMappings[i] = new RelationColumnMapping(mapping.fromField(),mapping.toField());
        }
        return columnMappings;
    }
}
