package dbgate.ermanagement.impl;

import dbgate.ermanagement.IERLayerStatistics;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 4/16/12
 * Time: 7:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ERLayerStatistics implements IERLayerStatistics
{
    private int globalSelectCount;
    private int globalInsertCount;
    private int globalUpdateCount;
    private int globalDeleteCount;
    private int globalPatchCount;
    private HashMap<Class,Integer> selectCount;
    private HashMap<Class,Integer> inertCount;
    private HashMap<Class,Integer> updateCount;
    private HashMap<Class,Integer> deleteCount;

    public ERLayerStatistics()
    {
        reset();
    }

    @Override
    public void reset()
    {
        globalSelectCount = 0;
        globalInsertCount = 0;
        globalUpdateCount = 0;
        globalDeleteCount = 0;
        globalPatchCount  = 0;
        selectCount        = new HashMap<Class,Integer>();
        inertCount        = new HashMap<Class,Integer>();
        updateCount       = new HashMap<Class,Integer>();
        deleteCount       = new HashMap<Class,Integer>();
    }

    @Override
    public int getSelectQueryCount()
    {
        return globalSelectCount;
    }

    @Override
    public int getInsertQueryCount()
    {
        return globalInsertCount;
    }

    @Override
    public int getUpdateQueryCount()
    {
        return globalUpdateCount;
    }

    @Override
    public int getDeleteQueryCount()
    {
        return globalDeleteCount;
    }

    @Override
    public int getDbPatchQueryCount()
    {
        return globalPatchCount;
    }

    @Override
    public int getSelectCount(Class type)
    {
        return getTypeCount(type,selectCount);
    }

    @Override
    public int getInsertCount(Class type)
    {
        return getTypeCount(type,inertCount);
    }

    @Override
    public int getUpdateCount(Class type)
    {
        return getTypeCount(type,updateCount);
    }

    @Override
    public int getDeleteCount(Class type)
    {
        return getTypeCount(type,deleteCount);
    }

    private int getTypeCount(Class type,HashMap<Class,Integer> typeCountMap)
    {
        if (typeCountMap.containsKey(type))
        {
            return typeCountMap.get(type);
        }
        return 0;
    }

    @Override
    public void registerSelect(Class type)
    {
        globalSelectCount++;
        registerCount(type, selectCount);
    }

    @Override
    public void registerInsert(Class type)
    {
        globalInsertCount++;
        registerCount(type, inertCount);
    }

    @Override
    public void registerUpdate(Class type)
    {
        globalUpdateCount++;
        registerCount(type, updateCount);
    }

    @Override
    public void registerDelete(Class type)
    {
        globalDeleteCount++;
        registerCount(type,updateCount);
    }

    private void registerCount(Class type,HashMap<Class,Integer> typeCountMap)
    {
        if (typeCountMap.containsKey(type))
        {
            int currentCount = typeCountMap.get(type);
            currentCount ++;
            typeCountMap.put(type,currentCount);
        }
    }

    @Override
    public void registerPatch()
    {
        globalPatchCount++;
    }
}
