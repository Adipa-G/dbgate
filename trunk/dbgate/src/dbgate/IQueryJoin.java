package dbgate;

import dbgate.ermanagement.query.QueryJoinExpressionType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 7/26/12
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IQueryJoin
{
    QueryJoinExpressionType getJoinExpressionType();
}