package dbgate.ermanagement.query;

import dbgate.IRODBClass;
import dbgate.ermanagement.*;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.ERLayer;

import java.sql.Connection;
import java.util.ArrayList;
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
    public Collection toList(Connection con) throws RetrievalException
    {
        return ERLayer.getSharedInstance().select(this,con);
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
