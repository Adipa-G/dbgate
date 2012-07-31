package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.IAbstractQuerySelection;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.query.QueryFromExpressionType;
import dbgate.ermanagement.query.QuerySelectionExpressionType;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractTypeQueryFrom implements IAbstractQueryFrom
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
    public QueryFromExpressionType getFromExpressionType()
    {
        return QueryFromExpressionType.TYPE;
    }

    @Override
    public String createSql()
    {
        try
        {
            return CacheManager.tableCache.getTableName(type);
        }
        catch (TableCacheMissException e)
        {
            try
            {
                ERDataManagerUtils.registerTypes((ServerRODBClass)type.newInstance());
                return CacheManager.tableCache.getTableName(type);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return "<unknown " + type.getCanonicalName() + ">";
        }
    }
}
