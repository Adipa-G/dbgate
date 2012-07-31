package dbgate.ermanagement.query;

import dbgate.ermanagement.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/29/12
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryStructure
{
    private boolean distinct;
    private long skip;
    private long fetch;

    private Collection<IQueryFrom> fromList;
    private Collection<IQueryJoin> joinList;
    private Collection<IQueryCondition> conditionList;
    private Collection<IQuerySelection> selectList;
    private Collection<IQueryGroup> groupList;
    private Collection<IQueryOrderBy> orderList;
    private Collection<IQueryGroupCondition> groupConditionList;

    public QueryStructure()
    {
        fromList = new ArrayList<IQueryFrom>();
        joinList = new ArrayList<IQueryJoin>();
        conditionList = new ArrayList<IQueryCondition>();
        selectList = new ArrayList<IQuerySelection>();
        groupList = new ArrayList<IQueryGroup>();
        orderList = new ArrayList<IQueryOrderBy>();
        groupConditionList = new ArrayList<IQueryGroupCondition>();
    }

    public boolean isDistinct()
    {
        return distinct;
    }

    public void setDistinct(boolean distinct)
    {
        this.distinct = distinct;
    }

    public long getFetch()
    {
        return fetch;
    }

    public void setFetch(long fetch)
    {
        this.fetch = fetch;
    }

    public long getSkip()
    {
        return skip;
    }

    public void setSkip(long skip)
    {
        this.skip = skip;
    }

    public Collection<IQueryFrom> getFromList()
    {
        return fromList;
    }

    public Collection<IQueryJoin> getJoinList()
    {
        return joinList;
    }

    public Collection<IQueryCondition> getConditionList()
    {
        return conditionList;
    }

    public Collection<IQuerySelection> getSelectList()
    {
        return selectList;
    }

    public Collection<IQueryGroup> getGroupList()
    {
        return groupList;
    }

    public Collection<IQueryOrderBy> getOrderList()
    {
        return orderList;
    }

    public Collection<IQueryGroupCondition> getGroupConditionList()
    {
        return groupConditionList;
    }
}
