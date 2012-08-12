package dbgate.ermanagement.ermapper.utils;

import dbgate.exceptions.common.EntityInstantiationException;
import dbgate.exceptions.common.MethodInvocationException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 12:42:01 PM
 */
public class ReflectionUtils
{
    public static boolean isImplementInterface(Class type,Class interfaceType)
    {
        if (type == interfaceType)
        {
            return true;
        }
        
        for (Class aClass : type.getInterfaces())
        {
            if (aClass == interfaceType)
            {
                return true;
            }
            if (isImplementInterface(aClass,interfaceType))
            {
                return true;
            }
        }

        Class superType = type.getSuperclass();
        while (superType != null)
        {
            if (isImplementInterface(superType,interfaceType))
            {
                return true;
            }
            superType = superType.getSuperclass();
        }
        return false;
    }

    public static Class[] getSuperTypesWithInterfacesImplemented(Class type,Class[] interfaceType)
    {
        List<Class> superTypes = new ArrayList<Class>();

        boolean interfacesMatched;
        Class iteratedType = type;
        do
        {
            interfacesMatched = true;
            for (Class iType : interfaceType)
            {
                interfacesMatched &= isImplementInterface(iteratedType,iType);
            }
            if (interfacesMatched)
            {
                superTypes.add(iteratedType);
            }
            iteratedType = iteratedType.getSuperclass();
        }
        while (interfacesMatched);

        Class[] ret = new Class[superTypes.size()];
        return superTypes.toArray(ret);
    }

    public static boolean isSubClassOf(Class type, Class superType)
    {
        if (type == superType)
        {
            return true;
        }

        if (type.getSuperclass() != null)
        {
            if (isSubClassOf(type.getSuperclass(), superType))
            {
                return true;
            }
        }
        return false;
    }

    public static Object getValue(Method getter,Object target) throws MethodInvocationException
    {
        try
        {
            return getter.invoke(target);
        }
        catch (Exception ex)
        {
            String message = String.format("Exception while trying to invoking getter %s of entity %s"
                    ,getter.getName(),target.getClass().getCanonicalName());
            throw new MethodInvocationException(message,ex);
        }
    }

    public static void setValue(Method setter,Object target,Object value) throws MethodInvocationException
    {
        try
        {
            setter.invoke(target,value);
        }
        catch (Exception ex)
        {
            String message = String.format("Exception while trying to invoking setter %s of entity %s"
                    ,setter.getName(),target.getClass().getCanonicalName());
            throw new MethodInvocationException(message,ex);
        }
    }

    public static Object createInstance(Class type) throws EntityInstantiationException
    {
        try
        {
            return type.newInstance();
        }
        catch (Exception ex)
        {
            String message = String.format("Exception while trying to create an instance of type %s"
                    ,type.getClass().getCanonicalName());
            throw new EntityInstantiationException(message,ex);
        }
    }
}
