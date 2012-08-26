package dbgate.caches.impl;

import dbgate.*;
import dbgate.caches.IEntityInfoCache;
import dbgate.ermanagement.ermapper.utils.ReflectionUtils;
import dbgate.exceptions.SequenceGeneratorInitializationException;
import dbgate.exceptions.common.EntityRegistrationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
    private final static HashMap<Class,EntityInfo> cache = new HashMap<>();
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
                register(type);
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
        try
        {
            if (!cache.containsKey(type))
            {
                register(type);
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(config.getLoggerName()).log(Level.SEVERE,ex.getMessage(),ex);
        }
        return cache.get(type);
    }

    @Override
    public void register(Class subType, String tableName, Collection<IField> fields)
    {
        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(subType,new Class[]{IReadOnlyEntity.class});
        Class immediateSuper = typeList.length > 1 ? typeList[1] : null;

        EntityInfo subEntityInfo = new EntityInfo(subType);
        subEntityInfo.setFields(fields);
        subEntityInfo.setTableName(tableName);

        if (immediateSuper != null
                && cache.containsKey(immediateSuper))
        {
            EntityInfo immediateSuperEntityInfo = cache.get(immediateSuper);
            subEntityInfo.setSuperEntityInfo(immediateSuperEntityInfo);
            immediateSuperEntityInfo.getSubEntityInfo().add(subEntityInfo);
        }

        synchronized (cache)
        {
            cache.put(subType,subEntityInfo);
        }
    }

    public void register(Class type) throws SequenceGeneratorInitializationException, EntityRegistrationException
    {
        if (cache.containsKey(type))
        {
            return;
        }
        HashMap<Class,EntityInfo> extracted = extractTableAndFieldInfo(type);
        synchronized (cache)
        {
            for (Class regType : extracted.keySet())
            {
                cache.put(regType,extracted.get(regType));
            }
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
                superEntityInfo.getSubEntityInfo().add(subEntity);
                continue;
            }

            String tableName = getTableName(regType, subType);
            Collection<IField> fields = getAllFields(regType, subType);

            if (tableName != null || fields.size() > 0)
            {
                EntityInfo entityInfo = new EntityInfo(regType);
                entityInfo.setFields(fields);
                entityInfo.setTableName(tableName);
                if (subEntity != null)
                {
                    subEntity.setSuperEntityInfo(entityInfo);
                    entityInfo.getSubEntityInfo().add(subEntity);
                }
                entityInfoMap.put(regType,entityInfo);
                subEntity = entityInfo;
            }
        }
        return entityInfoMap;
    }

    private static String getTableName(Class regType,Class subType) throws EntityRegistrationException
    {
        String tableName = getTableNameIfManagedClass(regType,subType);
        if (tableName == null)
        {
            Annotation[] annotations = regType.getAnnotations();
            for (Annotation annotation : annotations)
            {
                if (annotation instanceof TableInfo)
                {
                    TableInfo tableInfo = (TableInfo) annotation;
                    tableName = tableInfo.tableName();
                    break;
                }
            }
        }
        return tableName;
    }

    private static String getTableNameIfManagedClass(Class regType,Class subType) throws EntityRegistrationException
    {
        if (ReflectionUtils.isImplementInterface(regType, IManagedEntity.class))
        {
            try
            {
                IManagedEntity managedEntity = (IManagedEntity)subType.newInstance();
                return managedEntity.getTableNames().get(regType);
            }
            catch (Exception e)
            {
                throw  new EntityRegistrationException(String.format("Could not register type %s",regType.getCanonicalName()),e);
            }
        }
        return null;
    }

    private static Collection<IField> getAllFields(Class regType,Class subType) throws EntityRegistrationException,SequenceGeneratorInitializationException
    {
        Collection<IField> fields = getFieldsIfManagedClass(regType,subType);
        Class[] superTypes = ReflectionUtils.getSuperTypesWithInterfacesImplemented(regType,new Class[]{IReadOnlyEntity.class});

        for (int i = 0; i < superTypes.length; i++)
        {
            Class superType = superTypes[i];
            fields.addAll(getAllFields(superType,i > 0));
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

    private static Collection<IField> getAllFields(Class type,boolean superClass) throws SequenceGeneratorInitializationException
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
                    IColumn column = createColumnMapping(dbClassField, columnInfo);
                    if (superClass)
                    {
                        if (column.isSubClassCommonColumn())
                        {
                            fields.add(column);
                        }
                    }
                    else
                    {
                        fields.add(column);
                    }
                }
                else if (annotation instanceof ForeignKeyInfo)
                {
                    if (superClass)
                    {
                        continue;
                    }
                    ForeignKeyInfo foreignKeyInfo = (ForeignKeyInfo) annotation;
                    IRelation relation = createForeignKeyMapping(dbClassField, foreignKeyInfo);
                    fields.add(relation);
                }
                else if (annotation instanceof ForeignKeyInfoList)
                {
                    if (superClass)
                    {
                        continue;
                    }
                    ForeignKeyInfoList foreignKeyInfoList = (ForeignKeyInfoList) annotation;
                    for (ForeignKeyInfo foreignKeyInfo : foreignKeyInfoList.infoList())
                    {
                        IRelation relation = createForeignKeyMapping(dbClassField, foreignKeyInfo);
                        fields.add(relation);
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
        if (column.isReadFromSequence())
        {
            try
            {
                column.setSequenceGenerator((ISequenceGenerator) Class.forName(columnInfo.sequenceGeneratorClassName()).newInstance());
            }
            catch (Exception e)
            {

                throw new SequenceGeneratorInitializationException(String.format("Could not initialize sequence generator %s",
                                                                                 columnInfo.sequenceGeneratorClassName()),e);
            }
        }
        return column;
    }

    private static IRelation createForeignKeyMapping(Field dbClassField, ForeignKeyInfo foreignKeyInfo)
    {
        RelationColumnMapping[] objectMappings = new RelationColumnMapping[foreignKeyInfo.columnMappings().length];
        ForeignKeyColumnMapping[] annotationMappings = foreignKeyInfo.columnMappings();
        for (int i = 0, columnMappingsLength = annotationMappings.length; i < columnMappingsLength; i++)
        {
            ForeignKeyColumnMapping mapping = annotationMappings[i];
            objectMappings[i] = new RelationColumnMapping(mapping.fromField(),mapping.toField());
        }

        IRelation relation = new DefaultRelation(dbClassField.getName(),foreignKeyInfo.name()
                ,foreignKeyInfo.relatedObjectType(),objectMappings,foreignKeyInfo.updateRule()
                ,foreignKeyInfo.deleteRule(),foreignKeyInfo.reverseRelation()
                ,foreignKeyInfo.nonIdentifyingRelation(),foreignKeyInfo.lazy());

        return relation;
    }
}
