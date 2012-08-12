package dbgate.support.persistant.treetest;

import dbgate.IEntity;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 11:47:02 AM
 */
public interface ITreeTestRootEntity  extends IEntity
{
    int getIdCol();

    void setIdCol(int idCol);

    String getName();

    void setName(String name);

    Collection<ITreeTestOne2ManyEntity> getOne2ManyEntities();

    void setOne2ManyEntities(Collection<ITreeTestOne2ManyEntity> entities);

    ITreeTestOne2OneEntity getOne2OneEntity();

    void setOne2OneEntity(ITreeTestOne2OneEntity entity);
}
