package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.exceptions.ExpressionParsingException;
import dbgate.ermanagement.exceptions.common.ReadFromResultSetException;
import dbgate.ermanagement.exceptions.common.StatementExecutionException;
import dbgate.ermanagement.exceptions.common.StatementPreparingException;
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
    String createLoadQuery(String tableName, Collection<IDBColumn> dbColumns);

    String createInsertQuery(String tableName, Collection<IDBColumn> dbColumns);

    String createUpdateQuery(String tableName, Collection<IDBColumn> dbColumns);

    String createDeleteQuery(String tableName, Collection<IDBColumn> dbColumns);

    String createRelatedObjectsLoadQuery(IDBRelation relation);

    Object readFromResultSet(ResultSet rs, IDBColumn dbColumn) throws ReadFromResultSetException;

    void setToPreparedStatement(PreparedStatement ps,Object obj,int parameterIndex, IDBColumn dbColumn)
        throws StatementPreparingException;

    ResultSet createResultSet(Connection con, QueryExecInfo execInfo)
        throws StatementPreparingException,StatementExecutionException;

    QueryBuildInfo processQuery(QueryBuildInfo buildInfo,QueryStructure structure) throws ExpressionParsingException;
}
