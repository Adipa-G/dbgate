package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.query.QueryFromExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractTypeFrom implements IAbstractFrom
{
    private Class type;
    private String alias;

    public Class getType()
    {
        return type;
    }

    public void setType(Class type)
    {
        this.type = type;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    @Override
    public QueryFromExpressionType getFromExpressionType()
    {
        return QueryFromExpressionType.TYPE;
    }

    @Override
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo)
    {
        String sql = null;
        try
        {
            sql = CacheManager.tableCache.getTableName(type);
        }
        catch (TableCacheMissException e)
        {
            try
            {
                ERDataManagerUtils.registerTypes((ServerRODBClass)type.newInstance());
                sql = CacheManager.tableCache.getTableName(type);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return "<unknown " + type.getCanonicalName() + ">";
        }
        if (alias != null && alias.length() > 0)
        {
            sql = sql + " as " + alias;
            buildInfo.addTypeAlias(alias,type);
        }
        return sql;
    }
}
