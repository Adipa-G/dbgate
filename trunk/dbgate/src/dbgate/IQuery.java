package dbgate;

import dbgate.ermanagement.query.QueryStructure;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/26/12
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IQuery
{
    IQuery from(IQueryFrom queryFrom);

    IQuery join(IQueryJoin queryJoin);

    IQuery where(IQueryCondition queryCondition);

    QueryStructure getStructure();
}
