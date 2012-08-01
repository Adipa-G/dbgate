package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QuerySelectionExpressionType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractTypeSelection implements IAbstractSelection
{
    private Class type;

    public Class getType()
    {
        return type;
    }

    public void setType(Class type)
    {
        this.type = type;
    }

    @Override
    public QuerySelectionExpressionType getSelectionType()
    {
        return QuerySelectionExpressionType.TYPE;
    }

    @Override
    public String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo)
    {
        Hashtable<String,Object> aliases = buildInfo.getAliases();
        if (aliases.containsValue(type))
        {
            Set<String> keys = aliases.keySet();
            for (String key : keys)
            {
                if (aliases.get(key) == type)
                {
                    return key + ".*";
                }
            }
        }
        return "*";
    }

    @Override
    public Object retrieve(ResultSet rs, Connection con) throws RetrievalException
    {
        try
        {
            ServerRODBClass instance = (ServerRODBClass)type.newInstance();
            instance.retrieve(rs,con);
            return instance;
        }
        catch (Exception ex)
        {
            throw new RetrievalException(ex.getMessage(),ex);
        }
    }
}
