package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.ISelectionQuery;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.query.QueryStructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    String createRelatedObjectsLoadQuery(IDBRelation relation) throws TableCacheMissException, FieldCacheMissException;

    Object readFromResultSet(ResultSet rs, IDBColumn dbColumn) throws SQLException;

    void setToPreparedStatement(PreparedStatement ps,Object obj,int parameterIndex, IDBColumn dbColumn) throws SQLException;

    ResultSet createResultSet(Connection con, QueryExecInfo execInfo) throws SQLException;

    QueryExecInfo createExecInfo(Connection con, ISelectionQuery query) throws SQLException;

    QueryBuildInfo processQuery(QueryBuildInfo buildInfo,QueryStructure structure);
}
