package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.IRODBClass;
import dbgate.ServerRODBClass;
import dbgate.ermanagement.IQuerySelection;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.NoFieldsFoundException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.exceptions.SequenceGeneratorInitializationException;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.ERLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecInfo;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.QueryExecParam;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.query.QuerySelectionExpressionType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/31/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractTypeQuerySelection implements IAbstractQuerySelection
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
    public String createSql(QueryExecInfo execInfo)
    {
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
