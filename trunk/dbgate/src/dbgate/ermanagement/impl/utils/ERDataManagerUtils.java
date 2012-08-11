package dbgate.ermanagement.impl.utils;

import dbgate.DBColumnType;
import dbgate.DbGateException;
import dbgate.ServerDBClass;
import dbgate.ServerRODBClass;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.caches.impl.EntityInfo;
import dbgate.ermanagement.context.EntityFieldValue;
import dbgate.ermanagement.context.IEntityFieldValueList;
import dbgate.ermanagement.context.IFieldValueList;
import dbgate.ermanagement.context.ITypeFieldValueList;
import dbgate.ermanagement.context.impl.EntityFieldValueList;
import dbgate.ermanagement.context.impl.EntityRelationFieldValueList;
import dbgate.ermanagement.context.impl.EntityTypeFieldValueList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 24, 2011
 * Time: 10:14:22 PM
 */
public class ERDataManagerUtils
{
    public static Collection<ServerDBClass> getRelationEntities(ServerDBClass rootEntity, IDBRelation relation) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(rootEntity);
        Method getter = entityInfo.getGetter(relation.getAttributeName());
        Object value = ReflectionUtils.getValue(getter,rootEntity);

        Collection<ServerDBClass> treeEntities = new ArrayList<>();
        if (value instanceof Collection)
        {
            Collection collection = (Collection) value;
            for (Object o : collection)
            {
                if (o instanceof ServerDBClass
                        && ReflectionUtils.isSubClassOf(o.getClass(),relation.getRelatedObjectType()))
                {
                    treeEntities.add((ServerDBClass) o);
                }
            }
        }
        else if (value instanceof ServerDBClass
                        && ReflectionUtils.isSubClassOf(value.getClass(),relation.getRelatedObjectType()))
        {
            treeEntities.add((ServerDBClass) value);
        }
        return treeEntities;
    }

    public static IEntityFieldValueList extractEntityKeyValues(ServerRODBClass entity) throws DbGateException
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

    public static ITypeFieldValueList extractEntityTypeFieldValues(ServerRODBClass entity, Class type) throws DbGateException
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

    public static ITypeFieldValueList extractEntityTypeKeyValues(ServerRODBClass entity, Class type) throws DbGateException
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

    public static ITypeFieldValueList extractRelationKeyValues(ServerRODBClass child,IDBRelation relation)
            throws DbGateException
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

    private static Collection<EntityFieldValue> extractValues(ServerDBClass entity,boolean key,Class typeToLoad)
            throws DbGateException
    {
        Collection<EntityFieldValue> entityFieldValues = new ArrayList<>();
        EntityInfo parentEntityInfo = CacheManager.getEntityInfo(entity);
        EntityInfo entityInfo = parentEntityInfo;

        while (entityInfo != null)
        {
            if (typeToLoad != null && typeToLoad != entityInfo.getEntityType())
            {
                entityInfo = entityInfo.getSuperEntityInfo();
                continue;
            }

            Collection<IDBColumn> subLevelColumns = entityInfo.getColumns();
            for (IDBColumn subLevelColumn : subLevelColumns)
            {
                if (!key || (subLevelColumn.isKey() && key))
                {
                    Method getter = parentEntityInfo.getGetter(subLevelColumn.getAttributeName());
                    Object value = ReflectionUtils.getValue(getter,entity);

                    entityFieldValues.add(new EntityFieldValue(value,subLevelColumn));
                }
            }
            entityInfo = entityInfo.getSuperEntityInfo();
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
}
