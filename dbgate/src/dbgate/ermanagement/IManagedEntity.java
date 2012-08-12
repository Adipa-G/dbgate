package dbgate.ermanagement;

import dbgate.IEntity;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jul 5, 2008
 * Time: 2:56:37 PM
 */
public interface IManagedEntity extends IManagedReadOnlyEntity, IEntity
{
    Map<Class,String> getTableNames();
}
