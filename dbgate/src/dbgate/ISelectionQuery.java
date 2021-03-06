package dbgate;

import dbgate.ermanagement.query.*;
import dbgate.exceptions.RetrievalException;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/26/12
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISelectionQuery extends IQuery
{
    Collection toList(ITransaction tx) throws RetrievalException;

    @Override
    ISelectionQuery from(IQueryFrom queryFrom);

    @Override
    ISelectionQuery join(IQueryJoin queryJoin);

    ISelectionQuery distinct();

    ISelectionQuery skip(long records);

    ISelectionQuery fetch(long records);

    @Override
    ISelectionQuery where(IQueryCondition queryCondition);

    ISelectionQuery select(IQuerySelection querySelection);

    ISelectionQuery groupBy(IQueryGroup queryGroup);

    ISelectionQuery orderBy(IQueryOrderBy queryOrderBy);

    ISelectionQuery having(IQueryGroupCondition queryGroupCondition);
}
