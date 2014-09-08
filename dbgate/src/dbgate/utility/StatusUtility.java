package dbgate.utility;


import dbgate.EntityStatus;
import dbgate.IClientEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;


public class StatusUtility
{
    public static void setStatus(IClientEntity dbClass, EntityStatus status)
    {
        Collection<IClientEntity> usedItems = new ArrayList<IClientEntity>();
        setStatus(usedItems,dbClass,status);
    }

    private static void setStatus(Collection<IClientEntity> usedItems,IClientEntity dbClass, EntityStatus status)
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
                                if (o instanceof IClientEntity)
                                {
                                    setStatus(usedItems,(IClientEntity) o, status);
                                }
                            }
                        }
                        else if (object instanceof IClientEntity)
                        {
                            setStatus(usedItems,(IClientEntity) object, status);
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
                    return true;
                }
            }

        }
        else if ( !( obO instanceof IClientEntity) )
        {
            return false;
        }

        if ( ( obO instanceof IClientEntity) )
        {
            IClientEntity dbClass = (IClientEntity) obO;

            modified = dbClass.getStatus() == EntityStatus.DELETED
                    || dbClass.getStatus() == EntityStatus.NEW
                    || dbClass.getStatus() == EntityStatus.MODIFIED;
            if ( modified )
            {
	            return true;
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
                                        return true;
                                    }
                                }
                            }
                            else if (object instanceof IClientEntity)
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

    public static Collection<IClientEntity> getImmidiateChildrenAndClear(IClientEntity dbClass)
    {
        Collection<IClientEntity> children = new ArrayList<IClientEntity>();

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
                                if (o instanceof IClientEntity)
                                {
                                    children.add((IClientEntity) o);
                                }
                            }
                            collection.clear();
                        }
                        else if (object instanceof IClientEntity)
                        {
                            children.add((IClientEntity) object);
                            String setterName = method.getName().replace("get","set");
                            Method setter = objectClass.getMethod(setterName,method.getReturnType());
                            if (setter != null)
                            {
                                setter.invoke(dbClass, new Object[]{null});
                            }
                            else
                            {
                                //read only property
                                setStatus((IClientEntity) object, EntityStatus.UNMODIFIED);
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

        return children;
    }
}