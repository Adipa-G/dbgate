package dbgate.ermanagement.impl.utils;

import dbgate.DBColumnType;
import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.context.EntityFieldValue;
import dbgate.ermanagement.context.IEntityFieldValueList;
import dbgate.ermanagement.context.IFieldValueList;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.context.impl.*;
import dbgate.ermanagement.exceptions.EntityRegistrationException;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.SequenceGeneratorInitializationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 24, 2011
 * Time: 10:14:22 PM
 */
public class ERDataManagerUtils
{
    public static Collection<ServerDBClass> getRelationEntities(ServerDBClass parent, IDBRelation relation)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Method getter = CacheManager.methodCache.getGetter(parent.getClass(),relation.getAttributeName());
        Object value = getter.invoke(parent);

        Collection<ServerDBClass> fieldObjects = new ArrayList<ServerDBClass>();
        if (value instanceof Collection)
        {
            Collection collection = (Collection) value;
            for (Object o : collection)
            {
                if (o instanceof ServerDBClass
                        && ReflectionUtils.isSubClassOf(o.getClass(),relation.getRelatedObjectType()))
                {
                    fieldObjects.add((ServerDBClass) o);
                }
            }
        }
        else if (value instanceof ServerDBClass
                        && ReflectionUtils.isSubClassOf(value.getClass(),relation.getRelatedObjectType()))
        {
            fieldObjects.add((ServerDBClass) value);
        }
        return fieldObjects;
    }

    public static IEntityFieldValueList extractEntityKeyValues(ServerRODBClass entity) throws FieldCacheMissException
            , NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        EntityFieldValueList valueList = null;
        if (entity instanceof ServerDBClass)
        {
            valueList = new EntityFieldValueList(entity);
            ServerDBClass entityDbClass = (ServerDBClass) entity;
            valueList.getFieldValues().addAll(extractValues(entityDbClass,true,entity.getClass()));
        }
        return valueList;
    }

    public static ITypeFieldValueList extractTypeFieldValues(ServerRODBClass entity,Class type) throws FieldCacheMissException
            , NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        EntityTypeFieldValueList valueList = null;
        if (entity instanceof ServerDBClass)
        {
            valueList = new EntityTypeFieldValueList(type);
            ServerDBClass entityDbClass = (ServerDBClass) entity;
            valueList.getFieldValues().addAll(extractValues(entityDbClass,false,type));
        }
        return valueList;
    }

    public static ITypeFieldValueList extractTypeKeyValues(ServerRODBClass entity,Class type) throws FieldCacheMissException
            , NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        EntityTypeFieldValueList valueList = null;
        if (entity instanceof ServerDBClass)
        {
            valueList = new EntityTypeFieldValueList(type);
            ServerDBClass entityDbClass = (ServerDBClass) entity;
            valueList.getFieldValues().addAll(extractValues(entityDbClass,true,type));
        }
        return valueList;
    }

    public static ITypeFieldValueList extractRelationKeyValues(ServerRODBClass child,IDBRelation relation) throws FieldCacheMissException
            , NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        EntityRelationFieldValueList valueList = null;
        if (child instanceof ServerDBClass)
        {
            valueList = new EntityRelationFieldValueList(relation);
            ServerDBClass childDbClass = (ServerDBClass) child;
            valueList.getFieldValues().addAll(extractValues(childDbClass,true,null));
        }
        return valueList;
    }

    private static Collection<EntityFieldValue> extractValues(ServerDBClass serverDBClass,boolean key,Class typeToLoad) throws FieldCacheMissException
            , NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Collection<EntityFieldValue> entityFieldValues = new ArrayList<EntityFieldValue>();

        Class[] typeList = ReflectionUtils.getSuperTypesWithInterfacesImplemented(serverDBClass.getClass(),new Class[]{ServerDBClass.class});
        for (Class type : typeList)
        {
            if (typeToLoad != null && typeToLoad != type)
            {
                continue;
            }
            Collection<IDBColumn> subLevelColumns = CacheManager.fieldCache.getColumns(type);
            for (IDBColumn subLevelColumn : subLevelColumns)
            {
                if (!key || (subLevelColumn.isKey() && key))
                {
                    Method getter = CacheManager.methodCache.getGetter(serverDBClass.getClass(), subLevelColumn.getAttributeName());
                    Object value = getter.invoke(serverDBClass);

                    entityFieldValues.add(new EntityFieldValue(value,subLevelColumn));
                }
            }
        }
        return entityFieldValues;
    }

    public static Collection<ITypeFieldValueList> findDeletedChildren(Collection<ITypeFieldValueList> startListRelation
            ,Collection<ITypeFieldValueList> currentListRelation)
    {
        Collection<ITypeFieldValueList> deletedListRelation = new ArrayList<ITypeFieldValueList>();

        for (ITypeFieldValueList keyValueList : startListRelation)
        {
            boolean found  = false;
            for (ITypeFieldValueList relationKeyValueListCurrent : currentListRelation)
            {
                found = isTypeKeyEquals(keyValueList, relationKeyValueListCurrent);
                if (found)
                {
                    break;
                }
            }
            if (!found)
            {
                deletedListRelation.add(keyValueList);
            }
        }

        return deletedListRelation;
    }

    public static boolean isTypeKeyEquals(ITypeFieldValueList item1, ITypeFieldValueList item2)
    {
        return item1.getType() == item2.getType() && isValueKeyEquals(item1, item2);
    }

    public static boolean isEntityKeyEquals(IEntityFieldValueList item1, IEntityFieldValueList item2)
    {
        return item1.getEntity() == item2.getEntity() && isValueKeyEquals(item1, item2);
    }

    private static boolean isValueKeyEquals(IFieldValueList item1, IFieldValueList item2)
    {
        for (EntityFieldValue fieldValue1 : item1.getFieldValues())
        {
            boolean found  = false;
            for (EntityFieldValue fieldValue2 : item2.getFieldValues())
            {
                if (fieldValue1.getAttributeName().equals(fieldValue2.getAttributeName()))
                {
                    found = fieldValue1.getValue() == fieldValue2.getValue()
                            || (fieldValue1.getValue() != null && fieldValue1.getValue().equals(fieldValue2.getValue()));
                }
                if (found)
                {
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

    public static IDBColumn findColumnByAttribute(Collection<IDBColumn> columns,String attribute)
    {
        for (IDBColumn column : columns)
        {
            if (column.getAttributeName().equalsIgnoreCase(attribute))
            {
                return column;
            }
        }
        return null;
    }

    public static void reverse(Class[] types)
    {
        for (int left = 0, right = types.length - 1; left < right; left++, right--)
        {
            Class temp = types[left];
            types[left]  = types[right];
            types[right] = temp;
        }
    }

    public static void incrementVersion(ITypeFieldValueList fieldValues)
    {
        for (EntityFieldValue fieldValue : fieldValues.getFieldValues())
        {
            if (fieldValue.getDbColumn().getColumnType() == DBColumnType.VERSION)
            {
                Integer version = Integer.parseInt(fieldValue.getValue().toString());
                version++;
                fieldValue.setValue(version);
                break;
            }
        }
    }

    public static void registerType(Class type) throws EntityRegistrationException
            , SequenceGeneratorInitializationException
    {
        CacheManager.tableCache.register(type);
        CacheManager.fieldCache.register(type);
    }
}
