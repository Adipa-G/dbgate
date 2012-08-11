package dbgate.ermanagement.caches;

import dbgate.ermanagement.IDBColumn;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 25, 2010
 * Time: 8:31:00 PM
 */
public interface IMethodCache
{
    Method getGetter(Class type, String attributeName) throws NoSuchMethodException;

    Method getSetter(Class type,String attributeName, Class[] params) throws NoSuchMethodException;

    Method getSetter(Class type, IDBColumn dbColumn) throws NoSuchMethodException;

    void clear();
}
