package dbgate.ermanagement.caches.impl;

import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.IFieldCache;
import dbgate.ermanagement.exceptions.EntityRegistrationException;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.SequenceGeneratorInitializationException;
import dbgate.ermanagement.impl.utils.DBClassAttributeExtractionUtils;
import dbgate.ermanagement.impl.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 25, 2010
 * Time: 11:01:39 PM
 */
public class FieldCache implements IFieldCache
{
    private final static HashMap<String, Collection<IField>> columnCache = new HashMap<String, Collection<IField>>();

    @Override
    public Collection<IField> getFields(Class type) throws FieldCacheMissException
    {
        Collection<IField> retFields;
        String cacheKey = getCacheKey(type);

        if (columnCache.containsKey(cacheKey))
        {
            retFields = columnCache.get(cacheKey);
        }
        else
        {
            throw new FieldCacheMissException(String.format("No cache entry found for %s",type.getCanonicalName()));
        }
        return retFields;
    }

    @Override
    public Collection<IDBColumn> getColumns(Class type)  throws FieldCacheMissException
    {
        return getColumns(getFields(type),false);
    }

    @Override
    public Collection<IDBColumn> getKeys(Class type) throws FieldCacheMissException
    {
        return getColumns(getFields(type),true);
    }

    private Collection<IDBColumn> getColumns(Collection<IField> fields, boolean keysOnly)
    {
        Collection<IDBColumn> columns = new ArrayList<IDBColumn>();
        for (IField field : fields)
        {
            if (field instanceof IDBColumn)
            {
                IDBColumn dbColumn = (IDBColumn) field;
                if ((keysOnly && dbColumn.isKey()) || !keysOnly)
                {
                    columns.add(dbColumn);
                }
            }
        }
        return columns;
    }

    @Override
    public Collection<IDBRelation> getRelations(Class type) throws FieldCacheMissException
    {
        return getRelations(getFields(type));
    }

    private Collection<IDBRelation> getRelations(Collection<IField> fields)
    {
        Collection<IDBRelation> relations = new ArrayList<IDBRelation>();
        for (IField field : fields)
        {
            if (field instanceof IDBRelation)
            {
                IDBRelation dbRelation = (IDBRelation) field;
                relations.add(dbRelation);
            }
        }
        return relations;
    }

    @Override
    public void register(Class type, Collection<IField> fields)
    {
        String cacheKey = getCacheKey(type);
        if (columnCache.containsKey(cacheKey))
        {
            return;
        }
        synchronized (columnCache)
        {
            columnCache.put(cacheKey, fields);
        }
    }

    @Override
    public void register(Class type) throws SequenceGeneratorInitializationException,EntityRegistrationException
    {
        if (columnCache.containsKey(getCacheKey(type)))
        {
            return;
        }

        IManagedRODBClass managedDBClass = null;
        if (ReflectionUtils.isImplementInterface(type, IManagedRODBClass.class))
        {
            try
            {
                managedDBClass = (IManagedRODBClass)type.newInstance();
            }
            catch (Exception e)
            {
                throw  new EntityRegistrationException(String.format("Could not register type %s",type.getCanonicalName()),e);
            }
        }

        HashMap<String, Collection<IField>> tempStore = new HashMap<>();
        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(type,new Class[]{ServerRODBClass.class});
        for (Class regType : typeList)
        {
            String cacheKey = getCacheKey(regType);
            if (columnCache.containsKey(cacheKey))
            {
                continue;
            }

            Collection<IField> extractedFields = DBClassAttributeExtractionUtils.getAllFields(regType);
            Collection<IField> managedFields = managedDBClass != null
                    ? getFieldsForManagedClass(managedDBClass,regType)
                    : new ArrayList<IField>();

            if (managedFields != null)
            {
                extractedFields.addAll(managedFields);
            }
            tempStore.put(cacheKey,extractedFields );
        }

        if (tempStore.isEmpty())
        {
            throw new EntityRegistrationException(String.format("Can't find list of fields for type %s",type.getCanonicalName()));
        }

        synchronized (columnCache)
        {
            for (String cacheKey : tempStore.keySet())
            {
                columnCache.put(cacheKey,tempStore.get(cacheKey));
            }
        }
    }

    private String getCacheKey(Class type)
    {
        return type.getCanonicalName();
    }

    private Collection<IField> getFieldsForManagedClass(IManagedRODBClass entity,Class type)
    {
        Collection<IField> fields = new ArrayList<>();

        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(type,new Class[]{ServerRODBClass.class});
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
                    if (field instanceof IDBColumn)
                    {
                        IDBColumn column = (IDBColumn) field;
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

    @Override
    public void clear()
    {
        columnCache.clear();
    }
}
