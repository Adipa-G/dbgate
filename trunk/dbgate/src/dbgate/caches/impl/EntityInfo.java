package dbgate.caches.impl;

import dbgate.*;
import dbgate.IColumn;
import dbgate.IRelation;
import dbgate.IField;
import dbgate.exceptions.common.MethodNotFoundException;
import dbgate.exceptions.query.QueryBuildingException;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/11/12
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityInfo
{
    private Class entityType;
    private String tableName;
    private EntityInfo superEntityInfo;

    private final HashMap<String,Method> methodMap;
    private final Collection<IColumn> columns;
    private final Collection<IRelation> relations;
    private final HashMap<String,String> queries;

    public EntityInfo(Class entityType)
    {
        this.entityType = entityType;
        this.columns = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.methodMap = new HashMap<>();
        this.queries = new HashMap<>();
    }

    public Class getEntityType()
    {
        return entityType;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public EntityInfo getSuperEntityInfo()
    {
        return superEntityInfo;
    }

    public void setSuperEntityInfo(EntityInfo superEntityInfo)
    {
        this.superEntityInfo = superEntityInfo;
    }

    public Collection<IColumn> getColumns()
    {
        return columns;
    }

    public Collection<IRelation> getRelations()
    {
        return relations;
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

    public HashMap<String, String> getQueries()
    {
        return queries;
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

    public String getLoadQuery(IDBLayer dbLayer) throws QueryBuildingException
    {
        String queryId = "LOAD";
        String query = getQuery(queryId);
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createLoadQuery(tableName,columns);
            setQuery(queryId,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Load Query building failed for table %s class %s",tableName,entityType.getCanonicalName()));
        }
        return query;
    }

    public String getInsertQuery(IDBLayer dbLayer) throws QueryBuildingException
    {
        String queryId = "INSERT";
        String query = getQuery(queryId);
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createInsertQuery(tableName,columns);
            setQuery(queryId,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Insert Query building failed for table %s class %s",tableName,entityType.getCanonicalName()));
        }
        return query;
    }

    public String getUpdateQuery(IDBLayer dbLayer) throws QueryBuildingException
    {
        String queryId = "UPDATE";
        String query = getQuery(queryId);
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createUpdateQuery(tableName, columns);
            setQuery(queryId,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Update Query building failed for table %s class %s",tableName,entityType.getCanonicalName()));
        }
        return query;
    }

    public String getDeleteQuery(IDBLayer dbLayer) throws QueryBuildingException
    {
        String queryId = "DELETE";
        String query = getQuery(queryId);
        if (query == null)
        {
            query = dbLayer.getDataManipulate().createDeleteQuery(tableName, columns);
            setQuery(queryId,query);
        }
        if (query == null)
        {
            throw new QueryBuildingException(String.format("Delete Query building failed for table %s class %s",tableName,entityType.getCanonicalName()));
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
            throw new QueryBuildingException(String.format("Child loading Query building failed for table %s class %s child object type %s",tableName,entityType.getCanonicalName(),relation.getRelatedObjectType().getCanonicalName()));
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
        if (methodMap.containsKey(cacheKey))
        {
            return methodMap.get(cacheKey);
        }

        String getterName = "get" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
        try
        {
            return getGetterByNameAndRegisterInCache(cacheKey,getterName);
        }
        catch (MethodNotFoundException e)
        {
            getterName = "is" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
            return getGetterByNameAndRegisterInCache(cacheKey,getterName);
        }
    }

    private Method getGetterByNameAndRegisterInCache(String cacheKey,String getterName) throws MethodNotFoundException
    {
        Method method;
        try
        {
            method = entityType.getMethod(getterName);
        }
        catch (Exception ex)
        {
            String message = String.format("Could not find method %s of class %s",getterName,entityType.getCanonicalName());
            throw new MethodNotFoundException(message,ex);
        }
        synchronized (methodMap)
        {
            methodMap.put(cacheKey,method);
        }
        return method;
    }

    public Method getSetter(String attributeName, Class[] params) throws MethodNotFoundException
    {
        String cacheKey = createCacheKey(false,attributeName);
        if (methodMap.containsKey(cacheKey))
        {
            return methodMap.get(cacheKey);
        }

        String setterName = "set" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
        Method method;
        try
        {
            method = entityType.getMethod(setterName,params);
        }
        catch (Exception ex)
        {
            String message = String.format("Could not find method %s of class %s",setterName,entityType.getCanonicalName());
            throw new MethodNotFoundException(message,ex);
        }
        synchronized (methodMap)
        {
            methodMap.put(cacheKey,method);
        }
        return method;
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
