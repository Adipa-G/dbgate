package dbgate.ermanagement.caches.impl;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jul 5, 2008
 * Time: 8:27:50 PM
 */
public class QueryHolder
{
    private HashMap<String,String> queryMap;

    public QueryHolder()
    {
        queryMap = new HashMap<String, String>();
    }

    public void setQuery(String id,String query)
    {
        if (queryMap.containsKey(id))
        {
            queryMap.remove(id);
        }
        queryMap.put(query,query);
    }

    public String getQuery(String id)
    {
        if (queryMap.containsKey(id))
        {
            return queryMap.get(id);
        }
        return null;
    }
}
