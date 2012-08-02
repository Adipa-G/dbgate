package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query;

import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecInfo;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryBuildInfo
{
    private QueryExecInfo execInfo;
    private String currentQueryId;
    private Hashtable<String,Object> aliases;

    public QueryBuildInfo()
    {
        execInfo = new QueryExecInfo();
        aliases = new Hashtable<>();
    }

    public QueryExecInfo getExecInfo()
    {
        return execInfo;
    }

    public void setCurrentQueryId(String currentQueryId)
    {
        this.currentQueryId = currentQueryId;
    }

    public void addTypeAlias(String alias,Class type)
    {
        aliases.put(currentQueryId + alias,type);
    }

    public void addQueryAlias(String alias,ISelectionQuery query)
    {
        aliases.put(currentQueryId + alias,query);
    }

    public void addUnionAlias(String alias)
    {
        aliases.put(currentQueryId + alias,"UNION");
    }

    public String getAlias(Object value)
    {
        for (String key : aliases.keySet())
        {
            if (aliases.get(key) == value
                    && key.startsWith(currentQueryId))
            {
                return key.replace(currentQueryId,"");
            }
        }
        return null;
    }
}