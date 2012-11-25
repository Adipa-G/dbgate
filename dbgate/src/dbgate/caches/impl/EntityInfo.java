package dbgate.caches.impl;

import dbgate.*;
import dbgate.caches.CacheManager;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.exceptions.common.MethodNotFoundException;
import dbgate.exceptions.query.QueryBuildingException;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityInfo
{
    private boolean relationColumnsPopulated;
    private Class entityType;
    private ITable tableInfo;
    private EntityInfo superEntityInfo;
    private Collection<EntityInfo> subEntityInfo;

    private final HashMap<String,Method> methodMap;
    private final Collection<IColumn> columns;
    private final Collection<IRelation> relations;
    private final Collection<EntityRelationColumnInfo> relationColumnInfoList;
    private final HashMap<String,String> queries;

    public EntityInfo(Class entityType)
    {
        this.relationColumnsPopulated = false;
        this.entityType = entityType;
        this.subEntityInfo = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.relationColumnInfoList = new ArrayList<>();
        this.methodMap = new HashMap<>();
        this.queries = new HashMap<>();
    }

    public Class getEntityType()
    {
        return entityType;
    }

    public ITable getTableInfo()
    {
        return tableInfo;
    }

    public void setTableInfo(ITable tableInfo)
    {
        this.tableInfo = tableInfo;
    }

    public EntityInfo getSuperEntityInfo()
    {
        return superEntityInfo;
    }

    public void setSuperEntityInfo(EntityInfo superEntityInfo)
    {
        this.superEntityInfo = superEntityInfo;
    }

    public Collection<EntityInfo> getSubEntityInfo()
    {
        return Collections.unmodifiableCollection(subEntityInfo);
    }

    public void addSubEntityInfo(EntityInfo subInfo)
    {
        subEntityInfo.add(subInfo);
    }

    public Collection<IColumn> getColumns()
    {
        populateRelationColumns();
        return Collections.unmodifiableCollection(columns);
    }

    public Collection<IRelation> getRelations()
    {
        return Collections.unmodifiableCollection(relations);
    }

    public Collection<EntityRelationColumnInfo> getRelationColumnInfoList()
    {
        return Collections.unmodifiableCollection(relationColumnInfoList);
    }

    public EntityRelationColumnInfo findRelationColumnInfo(String attributeName)
    {
        for (EntityRelationColumnInfo relationColumnInfo : relationColumnInfoList)
        {
            if (relationColumnInfo.getColumn().getAttributeName().equals(attributeName))
            {
                return relationColumnInfo;
            }
        }
        return null;
    }

    public IColumn findColumnByAttribute(String attributeName)
    {
        for (IColumn column : columns)
        {
            if (column.getAttributeName().equals(attributeName))
            {
                return column;
            }
        }
        return null;
    }

    public Collection<IColumn> getKeys()
    {
        Collection<IColumn> keys = new ArrayList<>();
        for (IColumn column : columns)
        {
            if (column.isKey())
            {
                keys.add(column);
            }
        }
        return keys;
    }

    public Map<String, String> getQueries()
    {
        return Collections.unmodifiableMap(queries);
    }

    public void setFields(Collection<IField> fields)
    {
        for (IField field : fields)
        {
            if (field instanceof IColumn)
            {
                IColumn dbColumn = (IColumn) field;
                columns.add(dbColumn);
            }
            else if (field instanceof IRelation)
            {
                relations.add((IRelation) field);
            }
        }
    }
    
    private void populateRelationColumns()
    {
        if (relationColumnsPopulated)
            return;

        for (IRelation relation : relations)
        {
            boolean found = hasManualRelationColumnsDefined(relation);

            if (!found)
            {
                createRelationColumns(relation);
            }
        }
    }

    private boolean hasManualRelationColumnsDefined(IRelation relation)
    {
        boolean found = false;
        for (RelationColumnMapping mapping : relation.getTableColumnMappings())
        {
            for (IColumn column : columns)
            {
                if (column.getAttributeName().equals(mapping.getFromField()))
                {
                    found = true;
                    break;
                }
            }
            if (found)
                break;
        }
        return found;
    }

    private void createRelationColumns(IRelation relation)
    {
        EntityInfo relationInfo = CacheManager.getEntityInfo(relation.getRelatedObjectType());
        while (relationInfo != null)
        {
            Collection<IColumn> relationKeys = relationInfo.getKeys();
            for (IColumn relationKey : relationKeys)
            {
                RelationColumnMapping matchingMapping = null;
                for (RelationColumnMapping mapping : relation.getTableColumnMappings())
                {
                    if (mapping.getToField().equals(relationKey.getAttributeName()))
                    {
                        matchingMapping = mapping;
                        break;
                    }
                }

                IColumn cloned = relationKey.clone();
                cloned.setKey(false);
                if (matchingMapping != null)
                {
                    cloned.setAttributeName(matchingMapping.getFromField());
                    cloned.setColumnName(AbstractColumn.predictColumnName(matchingMapping.getFromField()));
                }
                cloned.setNullable(relation.isNullable());
                columns.add(cloned);
                relationColumnInfoList.add(new EntityRelationColumnInfo(cloned,relation,matchingMapping));
            }
            relationInfo = relationInfo.getSuperEntityInfo();
        }
    }

    public String getLoadQuery(IDBLayer dbLayer) throws QueryBuildingException
    {
        String queryId = "LOAD";
        String query = getQuery(queryId);
        if (query == null)
        {
            populateRelationColumns();
            query = dbLayer.getDataManipulate().createLoadQuery(tableInfo.getTableName(),columns);
            setQuery(queryId,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Load Query building failed for table %s class %s",tableInfo,entityType.getCanonicalName()));
        }
        return query;
    }

    public String getInsertQuery(IDBLayer dbLayer) throws QueryBuildingException
    {
        String queryId = "INSERT";
        String query = getQuery(queryId);
        if (query == null)
        {
            populateRelationColumns();
            query = dbLayer.getDataManipulate().createInsertQuery(tableInfo.getTableName(),columns);
            setQuery(queryId,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Insert Query building failed for table %s class %s",tableInfo,entityType.getCanonicalName()));
        }
        return query;
    }

    public String getUpdateQuery(IDBLayer dbLayer) throws QueryBuildingException
    {
        String queryId = "UPDATE";
        String query = getQuery(queryId);
        if (query == null)
        {
            populateRelationColumns();
            query = dbLayer.getDataManipulate().createUpdateQuery(tableInfo.getTableName(), columns);
            setQuery(queryId,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Update Query building failed for table %s class %s",tableInfo,entityType.getCanonicalName()));
        }
        return query;
    }

    public String getDeleteQuery(IDBLayer dbLayer) throws QueryBuildingException
    {
        String queryId = "DELETE";
        String query = getQuery(queryId);
        if (query == null)
        {
            populateRelationColumns();
            query = dbLayer.getDataManipulate().createDeleteQuery(tableInfo.getTableName(), columns);
            setQuery(queryId,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Delete Query building failed for table %s class %s",tableInfo,entityType.getCanonicalName()));
        }
        return query;
    }

    public String getRelationObjectLoad(IDBLayer dbLayer, IRelation relation)
                throws QueryBuildingException
    {
        String queryId = relation.getRelationshipName() + "_" + relation.getRelatedObjectType().getCanonicalName();
        String query = getQuery(queryId);
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createRelatedObjectsLoadQuery(relation);
            setQuery(queryId,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Child loading Query building failed for table %s class %s child object type %s",tableInfo,entityType.getCanonicalName(),relation.getRelatedObjectType().getCanonicalName()));
        }
        return query;
    }

    private String getQuery(String id)
    {
        return queries.get(id);
    }

    private void setQuery(String id,String query)
    {
        synchronized (queries)
        {
            queries.put(id,query);
        }
    }

    public Method getGetter(String attributeName) throws MethodNotFoundException
    {
        String cacheKey = createCacheKey(true,attributeName);
        Method method = getFromMapIfExists(cacheKey);

        if (method == null)
        {
            String getterName = "get" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
            try
            {
                method = getGetterByNameAndRegisterInCache(cacheKey,getterName);
            }
            catch (MethodNotFoundException e)
            {
                getterName = "is" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
                method = getGetterByNameAndRegisterInCache(cacheKey,getterName);
            }
        }
        return method;
    }

    private Method getGetterByNameAndRegisterInCache(String cacheKey,String getterName) throws MethodNotFoundException
    {
        Method method = getMethod(getterName,new Class[]{});
        addToMap(cacheKey, method);
        return method;
    }

    public Method getSetter(String attributeName, Class[] params) throws MethodNotFoundException
    {
        String cacheKey = createCacheKey(false,attributeName);
        Method method = getFromMapIfExists(cacheKey);
        if (method == null)
        {
            String setterName = "set" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
            method = getMethod(setterName,params);
            addToMap(cacheKey, method);
        }
        return method;
    }

    private Method getFromMapIfExists(String cacheKey)
    {
        if (methodMap.containsKey(cacheKey))
            return methodMap.get(cacheKey);

        return null;
    }

    private void addToMap(String cacheKey, Method method)
    {
        synchronized (methodMap)
        {
            methodMap.put(cacheKey,method);
        }
    }

    private Method getMethod(String name,Class[] params) throws MethodNotFoundException
    {
        try
        {
            return entityType.getMethod(name, params);
        }
        catch (Exception ex)
        {
            String message = String.format("Could not find method %s of class %s",name,entityType.getCanonicalName());
            throw new MethodNotFoundException(message,ex);
        }
    }

    public Method getSetter(IColumn dbColumn) throws MethodNotFoundException
    {
        Class[] params = getParameters(dbColumn);
        if (params == null)
        {
            return null;
        }
        return getSetter(dbColumn.getAttributeName(),params);
    }

    private static Class[] getParameters(IColumn dbColumn)
    {
        Class[] params;
        switch (dbColumn.getColumnType())
        {
            case BOOLEAN:
                if (!dbColumn.isNullable())
                {
                    params = new Class[]{Boolean.TYPE};
                }
                else
                {
                    params = new Class[]{Boolean.class};
                }
                break;
            case CHAR:
                if (!dbColumn.isNullable())
                {
                    params = new Class[]{Character.TYPE};
                }
                else
                {
                    params = new Class[]{Character.class};
                }
                break;
            case DATE:
                params = new Class[]{DateWrapper.class};
                break;
            case DOUBLE:
                if (!dbColumn.isNullable())
                {
                    params = new Class[]{Double.TYPE};
                }
                else
                {
                    params = new Class[]{Double.class};
                }
                break;
            case FLOAT:
                if (!dbColumn.isNullable())
                {
                    params = new Class[]{Float.TYPE};
                }
                else
                {
                    params = new Class[]{Float.class};
                }
                break;
            case INTEGER:
            case VERSION:
                if (!dbColumn.isNullable())
                {
                    params = new Class[]{Integer.TYPE};
                }
                else
                {
                    params = new Class[]{Integer.class};
                }
                break;
            case LONG:
                if (!dbColumn.isNullable())
                {
                    params = new Class[]{Long.TYPE};
                }
                else
                {
                    params = new Class[]{Long.class};
                }
                break;
            case TIMESTAMP:
                params = new Class[]{TimeStampWrapper.class};
                break;
            case VARCHAR:
                params = new Class[]{String.class};
                break;
            default:
                params = null;
        }
        return params;
    }

    private String createCacheKey(boolean getter,String field)
    {
        return (getter?"get_":"set_") + field;
    }
}
