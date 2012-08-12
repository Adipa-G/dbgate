package dbgate.ermanagement.support.persistant.inheritancetest;

import dbgate.ServerDBClass;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 4:23:25 PM
 */
public interface IInheritanceTestSuperEntity extends ServerDBClass
{
    int getIdCol();

    void setIdCol(int idCol);

    String getName();

    void setName(String name);
}
