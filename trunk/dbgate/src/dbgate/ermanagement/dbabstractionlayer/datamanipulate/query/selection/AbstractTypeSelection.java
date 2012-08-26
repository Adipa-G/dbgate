package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.selection;

import dbgate.IReadOnlyEntity;
import dbgate.ITransaction;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QuerySelectionExpressionType;
import dbgate.exceptions.ExpressionParsingException;
import dbgate.exceptions.RetrievalException;

import java.sql.ResultSet;

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
    public String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        String alias = buildInfo.getAlias(type);
        if (alias != null)
        {
            return alias + ".*";
        }
        return "*";
    }

    @Override
    public Object retrieve(ResultSet rs, ITransaction tx,QueryBuildInfo buildInfo) throws RetrievalException
    {
        try
        {
            IReadOnlyEntity instance = (IReadOnlyEntity)type.newInstance();
            instance.retrieve(rs,tx);
            return instance;
        }
        catch (Exception ex)
        {
            throw new RetrievalException(ex.getMessage(),ex);
        }
    }
}
