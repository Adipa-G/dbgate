package dbgate.ermanagement;

import dbgate.ServerRODBClass;

import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 6, 2009
 * Time: 5:45:24 PM
 */
public interface IManagedRODBClass extends ServerRODBClass
{
    Map<Class,Collection<IField>> getFieldInfo();
}
