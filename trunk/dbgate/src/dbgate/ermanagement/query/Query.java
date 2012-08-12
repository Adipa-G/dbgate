package dbgate.ermanagement.query;

import dbgate.IQuery;
import dbgate.IQueryCondition;
import dbgate.IQueryFrom;
import dbgate.IQueryJoin;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/26/12
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Query implements IQuery
{
    protected QueryStructure structure;

    public Query()
    {
        structure = new QueryStructure();
    }

    @Override
    public IQuery from(IQueryFrom queryFrom)
    {
        structure.getFromList().add(queryFrom);
        return this;
    }

    @Override
    public IQuery join(IQueryJoin queryJoin)
    {
        structure.getJoinList().add(queryJoin);
        return this;
    }

    @Override
    public IQuery where(IQueryCondition queryCondition)
    {
        structure.getConditionList().add(queryCondition);
        return this;
    }

    public QueryStructure getStructure()
    {
        return structure;
    }
}
