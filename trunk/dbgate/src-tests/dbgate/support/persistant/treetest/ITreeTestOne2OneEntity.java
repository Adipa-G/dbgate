package dbgate.ermanagement.support.persistant.treetest;

import dbgate.ServerDBClass;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:51:19 AM
 */
public interface ITreeTestOne2OneEntity extends ServerDBClass
{
    int getIdCol();

    void setIdCol(int idCol);

    String getName();

    void setName(String name);
}