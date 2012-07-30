package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryExecInfo
{
    private String sql;
    private List<QueryParam> params;

    public QueryExecInfo()
    {
        params = new ArrayList<>();
    }

    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql = sql;
    }

    public List<QueryParam> getParams()
    {
        return params;
    }
}
