package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.IColumn;
import dbgate.IRelation;
import dbgate.exceptions.ExpressionParsingException;
import dbgate.exceptions.common.ReadFromResultSetException;
import dbgate.exceptions.common.StatementExecutionException;
import dbgate.exceptions.common.StatementPreparingException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryStructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 6:46:56 PM
 */
public interface IDataManipulate
{
    String createLoadQuery(String tableName, Collection<IColumn> dbColumns);

    String createInsertQuery(String tableName, Collection<IColumn> dbColumns);

    String createUpdateQuery(String tableName, Collection<IColumn> dbColumns);

    String createDeleteQuery(String tableName, Collection<IColumn> dbColumns);

    String createRelatedObjectsLoadQuery(IRelation relation);

    Object readFromResultSet(ResultSet rs, IColumn dbColumn) throws ReadFromResultSetException;

    void setToPreparedStatement(PreparedStatement ps,Object obj,int parameterIndex, IColumn dbColumn)
        throws StatementPreparingException;

    ResultSet createResultSet(Connection con, QueryExecInfo execInfo)
        throws StatementPreparingException,StatementExecutionException;

    QueryBuildInfo processQuery(QueryBuildInfo buildInfo,QueryStructure structure) throws ExpressionParsingException;
}
