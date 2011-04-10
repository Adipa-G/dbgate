package dbgate.utility.support;

import dbgate.DBClassStatus;
import dbgate.IDBClass;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 9:27:39 AM
 */
public class RootEntity implements IDBClass
{
    private DBClassStatus status;
    private LeafEntity leafEntityNotNull;
    private LeafEntity leafEntityNull;
    private ArrayList<LeafEntity> leafEntities;

    public RootEntity()
    {
        leafEntities = new ArrayList<LeafEntity>();
    }

    public DBClassStatus getStatus()
    {
        return status;
    }

    public void setStatus(DBClassStatus status)
    {
        this.status = status;
    }

    public LeafEntity getLeafEntityNotNull()
    {
        return leafEntityNotNull;
    }

    public void setLeafEntityNotNull(LeafEntity leafEntity)
    {
        this.leafEntityNotNull = leafEntity;
    }

    public ArrayList<LeafEntity> getLeafEntities()
    {
        return leafEntities;
    }

    public LeafEntity getLeafEntityNull()
    {
        return leafEntityNull;
    }

    public void setLeafEntityNull(LeafEntity leafEntityNull)
    {
        this.leafEntityNull = leafEntityNull;
    }

    public void setLeafEntities(ArrayList<LeafEntity> leafEntities)
    {
        this.leafEntities = leafEntities;
    }
}
