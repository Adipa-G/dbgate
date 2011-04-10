package dbgate.utility;


import dbgate.DBClassStatus;
import dbgate.IDBClass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;


public class StatusUtility
{
    private static final String fmt = "%24s: %s%n";

    public static void setStatus(IDBClass dbClass, DBClassStatus status)
    {
        Collection<IDBClass> usedItems = new ArrayList<IDBClass>();
        setStatus(usedItems,dbClass,status);
    }

    private static void setStatus(Collection<IDBClass> usedItems,IDBClass dbClass, DBClassStatus status)
    {
        if (dbClass == null || usedItems.contains(dbClass))
        {
            return;
        }

        usedItems.add(dbClass);
        dbClass.setStatus(status);

        Class objectClass = dbClass.getClass();
        Method[] methods = objectClass.getMethods();
        for (Method method : methods)
        {
            if (method.getName().startsWith("get"))
            {
                try
                {
                    Object object = method.invoke(dbClass);
                    
                    if (object != null)
                    {
                        if (object instanceof Collection)
                        {
                            Collection collection = (Collection) object;
                            for (Object o : collection)
                            {
                                if (o instanceof IDBClass)
                                {
                                    setStatus(usedItems,(IDBClass) o, status);
                                }
                            }
                        }
                        else if (object instanceof IDBClass)
                        {
                            setStatus(usedItems,(IDBClass) object, status);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isModified( Object obO )
    {
        Collection<Object> alreadyCheckedItems = new ArrayList<Object>();
        return isModified(alreadyCheckedItems,obO);
    }

    private static boolean isModified(Collection<Object> alreadyChecked , Object obO )
    {
        boolean modified = false;
        if ( obO == null || alreadyChecked.contains(obO) )
        {
            return false;
        }
        alreadyChecked.add(obO);

        if ( obO instanceof Collection )
        {
            Collection collection = ( Collection ) obO;
            for ( Object o : collection )
            {
                modified = isModified(alreadyChecked, o );
                if ( modified )
                {
                    return modified;
                }
            }

        }
        else if ( !( obO instanceof IDBClass) )
        {
            return false;
        }

        if ( ( obO instanceof IDBClass) )
        {
            IDBClass dbClass = (IDBClass) obO;

            modified = dbClass.getStatus() == DBClassStatus.DELETED
                    || dbClass.getStatus() == DBClassStatus.NEW
                    || dbClass.getStatus() == DBClassStatus.MODIFIED;
            if ( modified )
            {
                return modified;
            }

            Class objectClass = dbClass.getClass();
            Method[] methods = objectClass.getMethods();
            for (Method method : methods)
            {
                if (method.getName().startsWith("get"))
                {

                    try
                    {
                        Object object = method.invoke(dbClass);
                        if (object != null)
                        {
                            if (object instanceof Collection)
                            {
                                Collection collection = (Collection) object;
                                for (Object o : collection)
                                {
                                    modified = isModified(alreadyChecked,o);
                                    if (modified)
                                    {
                                        return modified;
                                    }
                                }
                            }
                            else if (object instanceof IDBClass)
                            {
                                return isModified(alreadyChecked,object);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return modified;
    }

    public static Collection<IDBClass> getImmidiateChildrenAndClear(IDBClass dbClass)
    {
        Collection<IDBClass> childs = new ArrayList<IDBClass>();

        Class objectClass = dbClass.getClass();
        Method[] methods = objectClass.getMethods();

        for (Method method : methods)
        {
            if (method.getName().startsWith("get")
                    && method.getParameterTypes().length == 0)
            {
                try
                {
                    Object object = method.invoke(dbClass);
                    if (object != null)
                    {
                        if (object instanceof Collection)
                        {
                            Collection collection = (Collection) object;
                            for (Object o : collection)
                            {
                                if (o instanceof IDBClass)
                                {
                                    childs.add((IDBClass) o);
                                }
                            }
                            collection.clear();
                        }
                        else if (object instanceof IDBClass)
                        {
                            childs.add((IDBClass) object);
                            String setterName = method.getName().replace("get","set");
                            Method setter = objectClass.getMethod(setterName,method.getReturnType());
                            if (setter != null)
                            {
                                setter.invoke(dbClass, new Object[]{null});
                            }
                            else
                            {
                                //read only property
                                setStatus((IDBClass) object, DBClassStatus.UNMODIFIED);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return childs;
    }
}