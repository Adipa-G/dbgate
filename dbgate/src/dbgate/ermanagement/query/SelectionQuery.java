package dbgate.ermanagement.query;

import dbgate.ISelectionQuery;
import dbgate.ITransaction;
import dbgate.exceptions.RetrievalException;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 7:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectionQuery extends Query implements ISelectionQuery
{
    public SelectionQuery()
    {
    }

    @Override
    public Collection toList(ITransaction tx) throws RetrievalException
    {
        return tx.getDbGate().select(this,tx);
    }

    @Override
    public ISelectionQuery distinct()
    {
        structure.setDistinct(true);
        return this;
    }

    @Override
    public ISelectionQuery fetch(long records)
    {
        structure.setFetch(records);
        return this;
    }

    @Override
    public ISelectionQuery skip(long records)
    {
        structure.setSkip(records);
        return this;
    }

    @Override
    public ISelectionQuery from(IQueryFrom queryFrom)
    {
        return (ISelectionQuery) super.from(queryFrom);
    }

    @Override
    public ISelectionQuery join(IQueryJoin queryJoin)
    {
        return (ISelectionQuery) super.join(queryJoin);
    }

    @Override
    public ISelectionQuery where(IQueryCondition queryCondition)
    {
        return (ISelectionQuery) super.where(queryCondition);
    }

    @Override
    public ISelectionQuery select(IQuerySelection querySelection)
    {
        structure.getSelectList().add(querySelection);
        return this;
    }

    @Override
    public ISelectionQuery groupBy(IQueryGroup queryGroup)
    {
        structure.getGroupList().add(queryGroup);
        return this;
    }

    @Override
    public ISelectionQuery orderBy(IQueryOrderBy queryOrderBy)
    {
        structure.getOrderList().add(queryOrderBy);
        return this;
    }

    @Override
    public ISelectionQuery having(IQueryGroupCondition queryGroupCondition)
    {
        structure.getGroupConditionList().add(queryGroupCondition);
        return this;
    }
}
