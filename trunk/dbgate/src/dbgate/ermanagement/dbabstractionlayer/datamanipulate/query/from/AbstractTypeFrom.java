package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.from;

import dbgate.caches.CacheManager;
import dbgate.caches.impl.EntityInfo;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryFromExpressionType;
import dbgate.exceptions.ExpressionParsingException;

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
    public String createSql(IDBLayer dbLayer, QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(type);
        String sql = entityInfo.getTableInfo().getTableName();

        if (alias != null && alias.length() > 0)
        {
            sql = sql + " as " + alias;
            buildInfo.addTypeAlias(alias,type);
        }
        return sql;
    }
}
