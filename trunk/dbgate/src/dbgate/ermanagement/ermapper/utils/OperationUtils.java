package dbgate.ermanagement.ermapper.utils;

import dbgate.*;
import dbgate.IColumn;
import dbgate.IRelation;
import dbgate.caches.CacheManager;
import dbgate.caches.impl.EntityInfo;
import dbgate.context.EntityFieldValue;
import dbgate.context.IEntityFieldValueList;
import dbgate.context.IFieldValueList;
import dbgate.context.ITypeFieldValueList;
import dbgate.context.impl.EntityFieldValueList;
import dbgate.context.impl.EntityRelationFieldValueList;
import dbgate.context.impl.EntityTypeFieldValueList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 24, 2011
 * Time: 10:14:22 PM
 */
public class OperationUtils
{
    public static Collection<IEntity> getRelationEntities(IEntity rootEntity, IRelation relation) throws DbGateException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(rootEntity);
        Method getter = entityInfo.getGetter(relation.getAttributeName());
        Object value = ReflectionUtils.getValue(getter,rootEntity);

        Collection<IEntity> treeEntities = new ArrayList<>();
        if (value instanceof Collection)
        {
            Collection collection = (Collection) value;
            for (Object o : collection)
            {
                if (o instanceof IEntity
                        && ReflectionUtils.isSubClassOf(o.getClass(),relation.getRelatedObjectType()))
                {
                    treeEntities.add((IEntity) o);
                }
            }
        }
        else if (value instanceof IEntity
                        && ReflectionUtils.isSubClassOf(value.getClass(),relation.getRelatedObjectType()))
        {
            treeEntities.add((IEntity) value);
        }
        return treeEntities;
    }

    public static IEntityFieldValueList extractEntityKeyValues(IReadOnlyEntity entity) throws DbGateException
    {
        EntityFieldValueList valueList = null;
        if (entity instanceof IEntity)
        {
            valueList = new EntityFieldValueList(entity);
            IEntity entityDbClass = (IEntity) entity;
            valueList.getFieldValues().addAll(extractValues(entityDbClass,true,entity.getClass()));
        }
        return valueList;
    }

    public static ITypeFieldValueList extractEntityTypeFieldValues(IReadOnlyEntity entity, Class type) throws DbGateException
    {
        EntityTypeFieldValueList valueList = null;
        if (entity instanceof IEntity)
        {
            valueList = new EntityTypeFieldValueList(type);
            IEntity entityDbClass = (IEntity) entity;
            valueList.getFieldValues().addAll(extractValues(entityDbClass,false,type));
        }
        return valueList;
    }

    public static ITypeFieldValueList extractEntityTypeKeyValues(IReadOnlyEntity entity, Class type) throws DbGateException
    {
        EntityTypeFieldValueList valueList = null;
        if (entity instanceof IEntity)
        {
            valueList = new EntityTypeFieldValueList(type);
            IEntity entityDbClass = (IEntity) entity;
            valueList.getFieldValues().addAll(extractValues(entityDbClass,true,type));
        }
        return valueList;
    }

    public static ITypeFieldValueList extractRelationKeyValues(IReadOnlyEntity child,IRelation relation)
            throws DbGateException
    {
        EntityRelationFieldValueList valueList = null;
        if (child instanceof IEntity)
        {
            valueList = new EntityRelationFieldValueList(relation);
            IEntity childDbClass = (IEntity) child;
            valueList.getFieldValues().addAll(extractValues(childDbClass,true,null));
        }
        return valueList;
    }

    private static Collection<EntityFieldValue> extractValues(IEntity entity,boolean key,Class typeToLoad)
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

            Collection<IColumn> subLevelColumns = entityInfo.getColumns();
            for (IColumn subLevelColumn : subLevelColumns)
            {
                if (!key || (subLevelColumn.isKey() && key))
                {
                    if (AlreadyHasTheColumnAdded(entityFieldValues,subLevelColumn))
                    {
                        continue;
                    }
                    Method getter = parentEntityInfo.getGetter(subLevelColumn.getAttributeName());
                    Object value = ReflectionUtils.getValue(getter,entity);

                    entityFieldValues.add(new EntityFieldValue(value,subLevelColumn));
                }
            }
            entityInfo = entityInfo.getSuperEntityInfo();
        }

        return entityFieldValues;
    }

    private static boolean AlreadyHasTheColumnAdded(Collection<EntityFieldValue> entityFieldValues,IColumn column)
    {
        for (EntityFieldValue fieldValue : entityFieldValues)
        {
            if (fieldValue.getDbColumn().getAttributeName().equals(column.getAttributeName()))
            {
                return true;
            }
        }
        return false;
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

    public static IColumn findColumnByAttribute(Collection<IColumn> columns,String attribute)
    {
        for (IColumn column : columns)
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
            if (fieldValue.getDbColumn().getColumnType() == ColumnType.VERSION)
            {
                Integer version = Integer.parseInt(fieldValue.getValue().toString());
                version++;
                fieldValue.setValue(version);
                break;
            }
        }
    }
}
