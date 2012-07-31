package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.DateWrapper;
import dbgate.TimeStampWrapper;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition.AbstractQueryConditionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition.IAbstractQueryCondition;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.AbstractQueryFromFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.IAbstractQueryFrom;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.AbstractQueryGroupFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.IAbstractQueryGroup;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractQuerySelectionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.IAbstractQuerySelection;
import dbgate.ermanagement.impl.utils.ERDataManagerUtils;
import dbgate.ermanagement.query.*;

import java.sql.*;
import java.util.*;

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
        initialize();
    }

    protected void initialize()
    {
        QuerySelection.setFactory(new AbstractQuerySelectionFactory());
        QueryFrom.setFactory(new AbstractQueryFromFactory());
        QueryCondition.setFactory(new AbstractQueryConditionFactory());
        QueryGroup.setFactory(new AbstractQueryGroupFactory());
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
    public ResultSet createResultSet(Connection con, final QueryExecInfo execInfo) throws SQLException
    {
        boolean storedProcedure = isStoredProcedure(execInfo.getSql());

        PreparedStatement ps;
        ResultSet rs;

        if (storedProcedure)
        {
            ps = con.prepareCall(execInfo.getSql());
        }
        else
        {
            ps = con.prepareStatement(execInfo.getSql());
        }

        List<QueryExecParam> params = execInfo.getParams();
        Collections.sort(params,new Comparator<QueryExecParam>()
        {
            @Override
            public int compare(QueryExecParam o1, QueryExecParam o2)
            {
                return (new Integer(o1.getIndex())).compareTo(o2.getIndex());
            }
        });

        for (int i = 0; i < (storedProcedure ? params.size() + 1 : params.size()); i++)
        {
            int count = i + 1;
            if (i == 0 && storedProcedure)
            {
                //todo check
                ((CallableStatement) ps).registerOutParameter(count, Types.REF);
                continue;
            }

            QueryExecParam param = storedProcedure ? params.get(i - 1) : params.get(i);
            int type = param.getType();
            Object value = param.getValue();

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

    @Override
    public QueryExecInfo createExecInfo(Connection con, ISelectionQuery query) throws SQLException
    {
        QueryStructure structure = query.getStructure();
        return processQuery(null,structure);
    }

    protected QueryExecInfo processQuery(QueryExecInfo execInfo,QueryStructure structure)
    {
        if (execInfo == null)
        {
            execInfo = new QueryExecInfo();
        }

        StringBuilder sb = new StringBuilder();
        processSelection(sb, execInfo, structure);
        processFrom(sb, execInfo, structure);
        processWhere(sb, execInfo, structure);
        processGroup(sb, execInfo, structure);
        //sb.append("HAVING ");
        //sb.append("ORDER BY ");

        execInfo.setSql(sb.toString());
        return execInfo;
    }

    private void processSelection(StringBuilder sb,QueryExecInfo execInfo,QueryStructure structure)
    {
        sb.append("SELECT ");

        Collection<IQuerySelection> selections = structure.getSelectList();
        boolean initial = true;
        for (IQuerySelection selection : selections)
        {
            if (!initial)
            {
                sb.append(",");
            }
            sb.append(CreateSelectionSql(selection));
            initial = false;
        }
    }

    protected String CreateSelectionSql(IQuerySelection selection)
    {
        if (selection != null)
        {
            return ((IAbstractQuerySelection)selection).createSql();
        }
        return "/*Incorrect Selection*/";
    }

    private void processFrom(StringBuilder sb, QueryExecInfo execInfo, QueryStructure structure)
    {
        sb.append(" FROM ");

        Collection<IQueryFrom> fromList = structure.getFromList();
        boolean initial = true;
        for (IQueryFrom from : fromList)
        {
            if (!initial)
            {
                sb.append(",");
            }
            sb.append(CreateFromSql(from));
            initial = false;
        }
    }

    protected String CreateFromSql(IQueryFrom from)
    {
        if (from != null)
        {
            return ((IAbstractQueryFrom)from).createSql();
        }
        return "/*Incorrect From*/";
    }

    private void processWhere(StringBuilder sb, QueryExecInfo execInfo, QueryStructure structure)
    {
        Collection<IQueryCondition> conditionList = structure.getConditionList();
        if (conditionList.size() == 0)
            return;

        sb.append(" WHERE ");

        boolean initial = true;
        for (IQueryCondition condition : conditionList)
        {
            if (!initial)
            {
                sb.append(" AND ");
            }
            sb.append(CreateWhereSql(condition));
            initial = false;
        }
    }

    protected String CreateWhereSql(IQueryCondition condition)
    {
        if (condition != null)
        {
            return ((IAbstractQueryCondition)condition).createSql();
        }
        return "/*Incorrect Where*/";
    }

    private void processGroup(StringBuilder sb, QueryExecInfo execInfo, QueryStructure structure)
    {
        Collection<IQueryGroup> groupList = structure.getGroupList();
        if (groupList.size() == 0)
            return;

        sb.append(" GROUP BY ");

        boolean initial = true;
        for (IQueryGroup group : groupList)
        {
            if (!initial)
            {
                sb.append(",");
            }
            sb.append(CreateGroupSql(group));
            initial = false;
        }
    }

    protected String CreateGroupSql(IQueryGroup group)
    {
        if (group != null)
        {
            return ((IAbstractQueryGroup)group).createSql();
        }
        return "/*Incorrect Group*/";
    }
}
