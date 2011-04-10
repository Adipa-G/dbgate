package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.DateWrapper;
import dbgate.TimeStampWrapper;
import dbgate.ermanagement.DBRelationColumnMapping;
import dbgate.ermanagement.IDBColumn;
import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 6:47:44 PM
 */
public class AbstractDataManipulate implements IDataManipulate
{
    private IDBLayer dbLayer;

    public AbstractDataManipulate(IDBLayer dbLayer)
    {
        this.dbLayer = dbLayer;
    }

    @Override
    public String createLoadQuery(String tableName, Collection<IDBColumn> dbColumns)
    {
        ArrayList<IDBColumn> keys = new ArrayList<IDBColumn>();

        for (IDBColumn dbColumn : dbColumns)
        {
            if (dbColumn.isKey())
            {
                keys.add(dbColumn);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(tableName);
        sb.append(" WHERE ");

        for (int i = 0; i < keys.size(); i++)
        {
            IDBColumn dbColumn = keys.get(i);
            if (i!=0)
            {
                sb.append(" AND ");
            }
            sb.append(dbColumn.getColumnName());
            sb.append("= ?");
        }

        return sb.toString();
    }

    @Override
    public String createInsertQuery(String tableName, Collection<IDBColumn> dbColumns)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tableName);
        sb.append(" (");

        Iterator<IDBColumn> iterator = dbColumns.iterator();
        for (int i = 0; i < dbColumns.size(); i++)
        {
            IDBColumn dbColumn = iterator.next();
            if (i!=0)
            {
                sb.append(",");
            }
            sb.append(dbColumn.getColumnName());
        }

        sb.append(") VALUES (");

        for (int i = 0; i < dbColumns.size(); i++)
        {
            if (i!=0)
            {
                sb.append(",");
            }
            sb.append("?");
        }

        sb.append(" )");

        return sb.toString();
    }

    @Override
    public String createUpdateQuery(String tableName, Collection<IDBColumn> dbColumns)
    {
        ArrayList<IDBColumn> keys = new ArrayList<IDBColumn>();
        ArrayList<IDBColumn> values = new ArrayList<IDBColumn>();

        for (IDBColumn dbColumn : dbColumns)
        {
            if (dbColumn.isKey())
            {
                keys.add(dbColumn);
            }
            else
            {
                values.add(dbColumn);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(tableName);
        sb.append(" SET ");

        for (int i = 0; i < values.size(); i++)
        {
            IDBColumn dbColumn = values.get(i);
            if (i!=0)
            {
                sb.append(",");
            }
            sb.append(dbColumn.getColumnName());
            sb.append(" = ?");
        }

        sb.append(" WHERE ");
        for (int i = 0; i < keys.size(); i++)
        {
            IDBColumn dbColumn = keys.get(i);
            if (i!=0)
            {
                sb.append(" AND ");
            }
            sb.append(dbColumn.getColumnName());
            sb.append("= ?");
        }

        return sb.toString();
    }

    @Override
    public String createDeleteQuery(String tableName, Collection<IDBColumn> dbColumns)
    {
        ArrayList<IDBColumn> keys = new ArrayList<IDBColumn>();

        for (IDBColumn dbColumn : dbColumns)
        {
            if (dbColumn.isKey())
            {
                keys.add(dbColumn);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(tableName);
        sb.append(" WHERE ");

        for (int i = 0; i < keys.size(); i++)
        {
            IDBColumn dbColumn = keys.get(i);
            if (i!=0)
            {
                sb.append(" AND ");
            }
            sb.append(dbColumn.getColumnName());
            sb.append("= ?");
        }

        return sb.toString();
    }

    @Override
    public String createRelatedObjectsLoadQuery(IDBRelation relation) throws TableCacheMissException, FieldCacheMissException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(CacheManager.tableCache.getTableName(relation.getRelatedObjectType()));
        sb.append(" WHERE ");

        for (int i = 0; i < relation.getTableColumnMappings().length; i++)
        {
            DBRelationColumnMapping mapping = relation.getTableColumnMappings()[i];
            if (i!=0)
            {
                sb.append(" AND ");
            }
            sb.append(ERDataManagerUtils.findColumnByAttribute(CacheManager.fieldCache.getColumns(relation.getRelatedObjectType()),mapping.getToField()).getColumnName());
            sb.append("= ?");
        }

        return sb.toString();
    }

    @Override
    public Object readFromResultSet(ResultSet rs, IDBColumn dbColumn) throws SQLException
    {
        switch (dbColumn.getColumnType())
        {
            case BOOLEAN:
                if (dbColumn.isNullable())
                {
                    Object obj = rs.getObject(dbColumn.getColumnName());
                    if (obj != null)
                    {
                        return rs.getBoolean(dbColumn.getColumnName());
                    }
                    return null;
                }
                else
                {
                    return rs.getBoolean(dbColumn.getColumnName());
                }
            case CHAR:
                if (dbColumn.isNullable())
                {
                    Object obj = rs.getObject(dbColumn.getColumnName());
                    if (obj != null)
                    {
                        return rs.getString(dbColumn.getColumnName()).charAt(0);
                    }
                    return null;
                }
                else
                {
                    return rs.getString(dbColumn.getColumnName()).charAt(0);
                }
            case DATE:
                if (dbColumn.isNullable())
                {
                    Object obj = rs.getObject(dbColumn.getColumnName());
                    if (obj != null)
                    {
                        return new DateWrapper(rs.getDate(dbColumn.getColumnName()));
                    }
                    return null;
                }
                else
                {
                    return new DateWrapper(rs.getDate(dbColumn.getColumnName()));
                }
            case DOUBLE:
                if (dbColumn.isNullable())
                {
                    Object obj = rs.getObject(dbColumn.getColumnName());
                    if (obj != null)
                    {
                        return rs.getDouble(dbColumn.getColumnName());
                    }
                    return null;
                }
                else
                {
                    return rs.getDouble(dbColumn.getColumnName());
                }
            case FLOAT:
                if (dbColumn.isNullable())
                {
                    Object obj = rs.getObject(dbColumn.getColumnName());
                    if (obj != null)
                    {
                        return rs.getFloat(dbColumn.getColumnName());
                    }
                    return null;
                }
                else
                {
                    return rs.getFloat(dbColumn.getColumnName());
                }
            case INTEGER:
            case VERSION:
                if (dbColumn.isNullable())
                {
                    Object obj = rs.getObject(dbColumn.getColumnName());
                    if (obj != null)
                    {
                        return rs.getInt(dbColumn.getColumnName());
                    }
                    return null;
                }
                else
                {
                    return rs.getInt(dbColumn.getColumnName());
                }
            case LONG:
                if (dbColumn.isNullable())
                {
                    Object obj = rs.getObject(dbColumn.getColumnName());
                    if (obj != null)
                    {
                        return rs.getLong(dbColumn.getColumnName());
                    }
                    return null;
                }
                else
                {
                    return rs.getLong(dbColumn.getColumnName());
                }
            case TIMESTAMP:
                if (dbColumn.isNullable())
                {
                    Object obj = rs.getObject(dbColumn.getColumnName());
                    if (obj != null)
                    {
                        return new TimeStampWrapper(rs.getTimestamp(dbColumn.getColumnName()));
                    }
                    return null;
                }
                else
                {
                    return new TimeStampWrapper(rs.getTimestamp(dbColumn.getColumnName()));
                }
            case VARCHAR:
                if (dbColumn.isNullable())
                {
                    Object obj = rs.getObject(dbColumn.getColumnName());
                    if (obj != null)
                    {
                        return rs.getString(dbColumn.getColumnName());
                    }
                    return null;
                }
                else
                {
                    return rs.getString(dbColumn.getColumnName());
                }
            default:
                return null;
        }
    }

    @Override
    public void setToPreparedStatement(PreparedStatement ps,Object obj,int parameterIndex, IDBColumn dbColumn) throws SQLException
    {
        switch (dbColumn.getColumnType())
        {
            case BOOLEAN:
                if (dbColumn.isNullable() && obj == null)
                {
                    ps.setNull(parameterIndex, Types.BOOLEAN);
                }
                else
                {
                    ps.setBoolean(parameterIndex,(Boolean)obj);
                }
                break;
            case CHAR:
                if (dbColumn.isNullable() && obj == null)
                {
                    ps.setNull(parameterIndex,Types.VARCHAR);
                }
                else
                {
                    ps.setString(parameterIndex,obj.toString());
                }
                break;
            case DATE:
                if (dbColumn.isNullable() && obj == null)
                {
                    ps.setNull(parameterIndex,Types.DATE);
                }
                else
                {
                    ps.setDate(parameterIndex,((DateWrapper)obj)._getSQLDate());
                }
                break;
            case DOUBLE:
                if (dbColumn.isNullable() && obj == null)
                {
                    ps.setNull(parameterIndex,Types.DOUBLE);
                }
                else
                {
                    ps.setDouble(parameterIndex,(Double)obj);
                }
                break;
            case FLOAT:
                if (dbColumn.isNullable() && obj == null)
                {
                    ps.setNull(parameterIndex,Types.FLOAT);
                }
                else
                {
                    ps.setFloat(parameterIndex,(Float)obj);
                }
                break;
            case INTEGER:
            case VERSION:
                if (dbColumn.isNullable() && obj == null)
                {
                    ps.setNull(parameterIndex,Types.INTEGER);
                }
                else
                {
                    ps.setInt(parameterIndex,(Integer)obj);
                }
                break;
            case LONG:
                if (dbColumn.isNullable() && obj == null)
                {
                    ps.setNull(parameterIndex,Types.BIGINT);
                }
                else
                {
                    ps.setLong(parameterIndex,(Long)obj);
                }
                break;
            case TIMESTAMP:
                if (dbColumn.isNullable() && obj == null)
                {
                    ps.setNull(parameterIndex,Types.TIMESTAMP);
                }
                else
                {
                    ps.setTimestamp(parameterIndex,((TimeStampWrapper)obj)._getSQLTimeStamp());
                }
                break;
            case VARCHAR:
                if (dbColumn.isNullable() && obj == null)
                {
                    ps.setNull(parameterIndex,Types.VARCHAR);
                }
                else
                {
                    ps.setString(parameterIndex,obj.toString());
                }
                break;
        }
    }

    @Override
    public ResultSet createResultSet(Connection con, final String sql, final int[] types, final Object[] values) throws SQLException
    {
        boolean storedProcedure = isStoredProcedure(sql);

        PreparedStatement ps;
        ResultSet rs;

        if (storedProcedure)
        {
            ps = con.prepareCall(sql);
        }
        else
        {
            ps = con.prepareStatement(sql);
        }

        for (int i = 0; i < (storedProcedure ? types.length + 1 : types.length); i++)
        {
            int count = i + 1;
            if (i == 0 && storedProcedure)
            {
                //todo check
                ((CallableStatement) ps).registerOutParameter(count, Types.REF);
                continue;
            }
            int type = storedProcedure ? types[i - 1] : types[i];
            Object value = storedProcedure ? values[i - 1] : values[i];

            if (value == null)
            {
                ps.setNull(count, type);
            }
            else
            {
                switch (type)
                {
                    case Types.BIGINT:
                    case Types.NUMERIC:
                        ps.setLong(count, (Long) value);
                        break;
                    case Types.BOOLEAN:
                        ps.setBoolean(count, (Boolean) value);
                        break;
                    case Types.CHAR:
                        ps.setInt(count, (Character) value);
                        break;
                    case Types.INTEGER:
                        ps.setInt(count, (Integer) value);
                        break;
                    case Types.DATE:
                        ps.setDate(count, ((DateWrapper) value)._getSQLDate());
                        break;
                    case Types.DOUBLE:
                        ps.setDouble(count, (Double) value);
                        break;
                    case Types.FLOAT:
                        ps.setFloat(count, (Float) value);
                        break;
                    case Types.TIMESTAMP:
                        ps.setTimestamp(count, ((TimeStampWrapper) value)._getSQLTimeStamp());
                        break;
                    case Types.VARCHAR:
                        ps.setString(count, (String) value);
                        break;
                }
            }
        }

        if (storedProcedure)
        {
            ps.execute();
            rs = ps.getResultSet();
        }
        else
        {
            rs = ps.executeQuery();
        }
        return rs;
    }

    private static  boolean isStoredProcedure(String sql)
    {
        return sql.replaceAll(" ", "").contains("begin?:=");
    }
}
