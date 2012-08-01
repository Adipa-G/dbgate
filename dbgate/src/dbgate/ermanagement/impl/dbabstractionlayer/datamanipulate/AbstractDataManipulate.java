package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.DBColumnType;
import dbgate.DateWrapper;
import dbgate.TimeStampWrapper;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.TableCacheMissException;
import dbgate.ermanagement.impl.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition.AbstractConditionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.condition.IAbstractCondition;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.AbstractFromFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.from.IAbstractFrom;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.AbstractGroupFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.group.IAbstractGroup;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.groupcondition.AbstractGroupConditionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.groupcondition.IAbstractGroupCondition;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join.AbstractJoinFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.join.IAbstractJoin;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby.AbstractOrderByFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.orderby.IAbstractOrderBy;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.AbstractSelectionFactory;
import dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate.query.selection.IAbstractSelection;
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
        QuerySelection.setFactory(new AbstractSelectionFactory());
        QueryFrom.setFactory(new AbstractFromFactory());
        QueryJoin.setFactory(new AbstractJoinFactory());
        QueryCondition.setFactory(new AbstractConditionFactory());
        QueryGroup.setFactory(new AbstractGroupFactory());
        QueryGroupCondition.setFactory(new AbstractGroupConditionFactory());
        QueryOrderBy.setFactory(new AbstractOrderByFactory());
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
        setToPreparedStatement(ps,obj,parameterIndex,dbColumn.isNullable(),dbColumn.getColumnType());
    }
    
    protected void setToPreparedStatement(PreparedStatement ps,Object obj,int parameterIndex,boolean nullable, DBColumnType dbColumnType) throws SQLException
    {
        switch (dbColumnType)
        {
            case BOOLEAN:
                if (nullable && obj == null)
                {
                    ps.setNull(parameterIndex, Types.BOOLEAN);
                }
                else
                {
                    ps.setBoolean(parameterIndex,(Boolean)obj);
                }
                break;
            case CHAR:
                if (nullable && obj == null)
                {
                    ps.setNull(parameterIndex,Types.VARCHAR);
                }
                else
                {
                    ps.setString(parameterIndex,obj.toString());
                }
                break;
            case DATE:
                if (nullable && obj == null)
                {
                    ps.setNull(parameterIndex,Types.DATE);
                }
                else
                {
                    ps.setDate(parameterIndex,((DateWrapper)obj)._getSQLDate());
                }
                break;
            case DOUBLE:
                if (nullable && obj == null)
                {
                    ps.setNull(parameterIndex,Types.DOUBLE);
                }
                else
                {
                    ps.setDouble(parameterIndex,(Double)obj);
                }
                break;
            case FLOAT:
                if (nullable && obj == null)
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
                if (nullable && obj == null)
                {
                    ps.setNull(parameterIndex,Types.INTEGER);
                }
                else
                {
                    ps.setInt(parameterIndex,(Integer)obj);
                }
                break;
            case LONG:
                if (nullable && obj == null)
                {
                    ps.setNull(parameterIndex,Types.BIGINT);
                }
                else
                {
                    ps.setLong(parameterIndex,(Long)obj);
                }
                break;
            case TIMESTAMP:
                if (nullable && obj == null)
                {
                    ps.setNull(parameterIndex,Types.TIMESTAMP);
                }
                else
                {
                    ps.setTimestamp(parameterIndex,((TimeStampWrapper)obj)._getSQLTimeStamp());
                }
                break;
            case VARCHAR:
                if (nullable && obj == null)
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
            DBColumnType type = param.getType();
            Object value = param.getValue();

            setToPreparedStatement(ps,value,count,value == null,type);
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

    public QueryBuildInfo processQuery(QueryBuildInfo buildInfo,QueryStructure structure)
    {
        if (buildInfo == null)
        {
            buildInfo = new QueryBuildInfo();
        }
        buildInfo.setCurrentQueryId(structure.getQueryId());

        StringBuilder sb = new StringBuilder();
        processFrom(sb, buildInfo, structure);
        processJoin(sb, buildInfo, structure);
        processWhere(sb, buildInfo, structure);
        processGroup(sb, buildInfo, structure);
        processGroupCondition(sb, buildInfo, structure);
        processOrderBy(sb, buildInfo, structure);

        addPagingClause(sb,buildInfo, structure);
        processSelection(sb, buildInfo, structure);

        buildInfo.getExecInfo().setSql(sb.toString());
        return buildInfo;
    }

    protected void addPagingClause(StringBuilder sb,QueryBuildInfo buildInfo,QueryStructure structure)
    {
        if (structure.getSkip() > 0)
        {
            sb.append(" OFFSET ? ROWS ");

            QueryExecParam param = new QueryExecParam();
            param.setIndex(buildInfo.getExecInfo().getParams().size());
            param.setType(DBColumnType.LONG);
            param.setValue(structure.getSkip());
            buildInfo.getExecInfo().getParams().add(param);
        }

        if (structure.getFetch() > 0)
        {
            sb.append(" FETCH NEXT ? ROWS ONLY ");

            QueryExecParam param = new QueryExecParam();
            param.setIndex(buildInfo.getExecInfo().getParams().size());
            param.setType(DBColumnType.LONG);
            param.setValue(structure.getFetch());
            buildInfo.getExecInfo().getParams().add(param);
        }
    }

    private void processSelection(StringBuilder querySb,QueryBuildInfo buildInfo,QueryStructure structure)
    {
        StringBuilder selectSb = new StringBuilder();
        selectSb.append("SELECT ");

        if (structure.getSelectList().size() == 0)
            selectSb.append(" * ");

        if (structure.isDistinct())
            selectSb.append(" DISTINCT ");

        Collection<IQuerySelection> selections = structure.getSelectList();
        boolean initial = true;
        for (IQuerySelection selection : selections)
        {
            if (!initial)
            {
                selectSb.append(",");
            }
            selectSb.append(CreateSelectionSql(selection, buildInfo));
            initial = false;
        }
        querySb.insert(0,selectSb.toString());
    }

    protected String CreateSelectionSql(IQuerySelection selection,QueryBuildInfo buildInfo)
    {
        if (selection != null)
        {
            return ((IAbstractSelection)selection).createSql(dbLayer,buildInfo);
        }
        return "/*Incorrect Selection*/";
    }

    private void processFrom(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure)
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
            sb.append(CreateFromSql(from,buildInfo));
            initial = false;
        }
    }

    protected String CreateFromSql(IQueryFrom from,QueryBuildInfo buildInfo)
    {
        if (from != null)
        {
            return ((IAbstractFrom)from).createSql(dbLayer,buildInfo);
        }
        return "/*Incorrect From*/";
    }

    private void processJoin(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure)
    {
        Collection<IQueryJoin> joinList = structure.getJoinList();
        if (joinList.size() == 0)
            return;

        for (IQueryJoin join : joinList)
        {
            sb.append(" ");
            sb.append(CreateJoinSql(join));
        }
    }

    protected String CreateJoinSql(IQueryJoin join)
    {
        if (join != null)
        {
            return ((IAbstractJoin)join).createSql();
        }
        return "/*Incorrect Join*/";
    }

    private void processWhere(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure)
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
            return ((IAbstractCondition)condition).createSql();
        }
        return "/*Incorrect Where*/";
    }

    private void processGroup(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure)
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
            return ((IAbstractGroup)group).createSql();
        }
        return "/*Incorrect Group*/";
    }

    private void processGroupCondition(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure)
    {
        Collection<IQueryGroupCondition> groupConditionList = structure.getGroupConditionList();
        if (groupConditionList.size() == 0)
            return;

        sb.append(" HAVING ");

        boolean initial = true;
        for (IQueryGroupCondition groupCondition : groupConditionList)
        {
            if (!initial)
            {
                sb.append(" AND ");
            }
            sb.append(CreateGroupConditionSql(groupCondition));
            initial = false;
        }
    }

    protected String CreateGroupConditionSql(IQueryGroupCondition groupCondition)
    {
        if (groupCondition != null)
        {
            return ((IAbstractGroupCondition)groupCondition).createSql();
        }
        return "/*Incorrect Group Condition*/";
    }

    private void processOrderBy(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure)
    {
        Collection<IQueryOrderBy> orderByCollection = structure.getOrderList();
        if (orderByCollection.size() == 0)
            return;

        sb.append(" ORDER BY ");

        boolean initial = true;
        for (IQueryOrderBy orderBy : orderByCollection)
        {
            if (!initial)
            {
                sb.append(",");
            }
            sb.append(CreateOrderBySql(orderBy));
            initial = false;
        }
    }

    protected String CreateOrderBySql(IQueryOrderBy orderBy)
    {
        if (orderBy != null)
        {
            return ((IAbstractOrderBy)orderBy).createSql();
        }
        return "/*Incorrect Order*/";
    }
}
