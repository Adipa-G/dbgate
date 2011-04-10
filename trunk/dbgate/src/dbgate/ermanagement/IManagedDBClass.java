package dbgate.ermanagement;

import dbgate.ServerDBClass;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jul 5, 2008
 * Time: 2:56:37 PM
 */
public interface IManagedDBClass extends IManagedRODBClass, ServerDBClass
{
    Map<Class,String> getTableNames();
}
