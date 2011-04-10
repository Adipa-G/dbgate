package dbgate.ermanagement.caches.impl;

import dbgate.DateWrapper;
import dbgate.GeneralLogger;
import dbgate.TimeStampWrapper;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.caches.IMethodCache;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 25, 2010
 * Time: 11:20:27 PM
 */
public class MethodCache implements IMethodCache
{
    private final static HashMap<String, Method> columnCache = new HashMap<String, Method>();

    @Override
    public Method getGetter(Object obj, String attributeName) throws NoSuchMethodException
    {
        String cacheKey = createCacheKey(obj,true,attributeName);
        if (columnCache.containsKey(cacheKey))
        {
            return columnCache.get(cacheKey);
        }
        
        String getterName = "get" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
        try
        {
            Method method = obj.getClass().getMethod(getterName);
            synchronized (columnCache)
            {
                columnCache.put(cacheKey,method);
            }
            return method;
        }
        catch (NoSuchMethodException e)
        {
            getterName = "is" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
            Method method = obj.getClass().getMethod(getterName);
            synchronized (columnCache)
            {
                columnCache.put(cacheKey,method);
            }
            return method;
        }
    }

    @Override
    public Method getSetter(Object obj, String attributeName, Class[] params) throws NoSuchMethodException
    {
        String cacheKey = createCacheKey(obj,false,attributeName);
        if (columnCache.containsKey(cacheKey))
        {
            return columnCache.get(cacheKey);
        }

        String setterName = "set" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
        Method method = obj.getClass().getMethod(setterName,params);
        synchronized (columnCache)
        {
            columnCache.put(cacheKey,method);
        }
        return method;
    }

    @Override
    public Method getSetter(Object obj, IDBColumn dbColumn) throws NoSuchMethodException
    {
        String cacheKey = createCacheKey(obj,false,dbColumn.getAttributeName());
        if (columnCache.containsKey(cacheKey))
        {
            return columnCache.get(cacheKey);
        }

        String setterName = "set" + dbColumn.getAttributeName().substring(0,1).toUpperCase() + dbColumn.getAttributeName().substring(1);
        Class[] params = getParameters(dbColumn);
        if (params == null)
        {
            return null;
        }
        Method method = obj.getClass().getMethod(setterName,params);
        synchronized (columnCache)
        {
            columnCache.put(cacheKey,method);
        }
        return method;
    }

    private static Class[] getParameters(IDBColumn dbColumn)
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

    public String createCacheKey(Object object,boolean getter,String field)
    {
        return object.getClass().getCanonicalName() + "_" + (getter?"get_":"set_") + field;
    }

    @Override
    public void clear()
    {
        columnCache.clear();
    }
}
