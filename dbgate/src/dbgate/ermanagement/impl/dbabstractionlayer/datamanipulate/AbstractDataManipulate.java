package dbgate.ermanagement.impl.dbabstractionlayer.datamanipulate;

import dbgate.ColumnType;
import dbgate.DateWrapper;
import dbgate.TimeStampWrapper;
import dbgate.ermanagement.*;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.caches.impl.EntityInfo;
import dbgate.ermanagement.exceptions.ExpressionParsingException;
import dbgate.ermanagement.exceptions.common.ReadFromResultSetException;
import dbgate.ermanagement.exceptions.common.StatementExecutionException;
import dbgate.ermanagement.exceptions.common.StatementPreparingException;
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
import dbgate.ermanagement.impl.utils.OperationUtils;
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
    public String createLoadQuery(String tableName, Collection<IColumn> dbColumns)
    {
        ArrayList<IColumn> keys = new ArrayList<IColumn>();

        for (IColumn dbColumn : dbColumns)
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
            IColumn dbColumn = keys.get(i);
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
    public String createInsertQuery(String tableName, Collection<IColumn> dbColumns)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tableName);
        sb.append(" (");

        Iterator<IColumn> iterator = dbColumns.iterator();
        for (int i = 0; i < dbColumns.size(); i++)
        {
            IColumn dbColumn = iterator.next();
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
    public String createUpdateQuery(String tableName, Collection<IColumn> dbColumns)
    {
        ArrayList<IColumn> keys = new ArrayList<IColumn>();
        ArrayList<IColumn> values = new ArrayList<IColumn>();

        for (IColumn dbColumn : dbColumns)
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
            IColumn dbColumn = values.get(i);
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
            IColumn dbColumn = keys.get(i);
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
    public String createDeleteQuery(String tableName, Collection<IColumn> dbColumns)
    {
        ArrayList<IColumn> keys = new ArrayList<IColumn>();

        for (IColumn dbColumn : dbColumns)
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
            IColumn dbColumn = keys.get(i);
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
    public String createRelatedObjectsLoadQuery(IRelation relation)
    {
        EntityInfo entityInfo = CacheManager.getEntityInfo(relation.getRelatedObjectType());

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(entityInfo.getTableName());
        sb.append(" WHERE ");

        for (int i = 0; i < relation.getTableColumnMappings().length; i++)
        {
            RelationColumnMapping mapping = relation.getTableColumnMappings()[i];
            if (i!=0)
            {
                sb.append(" AND ");
            }
            sb.append(OperationUtils.findColumnByAttribute(entityInfo.getColumns(), mapping.getToField()).getColumnName());
            sb.append("= ?");
        }

        return sb.toString();
    }

    @Override
    public Object readFromResultSet(ResultSet rs, IColumn dbColumn) throws ReadFromResultSetException
    {
        try
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
        catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying to read column named %s with type %s",dbColumn.getColumnName(),dbColumn.getColumnType());
            throw new ReadFromResultSetException(message,ex);
        }
    }

    @Override
    public void setToPreparedStatement(PreparedStatement ps,Object obj,int parameterIndex, IColumn dbColumn)
        throws StatementPreparingException
    {
        setToPreparedStatement(ps,obj,parameterIndex,dbColumn.isNullable(),dbColumn.getColumnType());
    }
    
    protected void setToPreparedStatement(PreparedStatement ps,Object obj,int parameterIndex,boolean canBeNull, ColumnType columnType)
        throws StatementPreparingException
    {
        try
        {
            switch (columnType)
            {
                case BOOLEAN:
                    if (canBeNull && obj == null)
                    {
                        ps.setNull(parameterIndex, Types.BOOLEAN);
                    }
                    else
                    {
                        ps.setBoolean(parameterIndex,(Boolean)obj);
                    }
                    break;
                case CHAR:
                    if (canBeNull && obj == null)
                    {
                        ps.setNull(parameterIndex,Types.VARCHAR);
                    }
                    else
                    {
                        ps.setString(parameterIndex,obj.toString());
                    }
                    break;
                case DATE:
                    if (canBeNull && obj == null)
                    {
                        ps.setNull(parameterIndex,Types.DATE);
                    }
                    else
                    {
                        ps.setDate(parameterIndex,((DateWrapper)obj)._getSQLDate());
                    }
                    break;
                case DOUBLE:
                    if (canBeNull && obj == null)
                    {
                        ps.setNull(parameterIndex,Types.DOUBLE);
                    }
                    else
                    {
                        ps.setDouble(parameterIndex,(Double)obj);
                    }
                    break;
                case FLOAT:
                    if (canBeNull && obj == null)
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
                    if (canBeNull && obj == null)
                    {
                        ps.setNull(parameterIndex,Types.INTEGER);
                    }
                    else
                    {
                        ps.setInt(parameterIndex,(Integer)obj);
                    }
                    break;
                case LONG:
                    if (canBeNull && obj == null)
                    {
                        ps.setNull(parameterIndex,Types.BIGINT);
                    }
                    else
                    {
                        ps.setLong(parameterIndex,(Long)obj);
                    }
                    break;
                case TIMESTAMP:
                    if (canBeNull && obj == null)
                    {
                        ps.setNull(parameterIndex,Types.TIMESTAMP);
                    }
                    else
                    {
                        ps.setTimestamp(parameterIndex,((TimeStampWrapper)obj)._getSQLTimeStamp());
                    }
                    break;
                case VARCHAR:
                    if (canBeNull && obj == null)
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
        catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying set parameter %s with value %s",parameterIndex,obj);
            throw new StatementPreparingException(message,ex);
        }
    }

    @Override
    public ResultSet createResultSet(Connection con, final QueryExecInfo execInfo) throws StatementPreparingException,StatementExecutionException
    {
        try
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
                ColumnType type = param.getType();
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
        catch (SQLException ex)
        {
            String message = String.format("SQL Exception while trying set executing %s",execInfo.getSql());
            throw new StatementExecutionException(message,ex);
        }
    }

    private static  boolean isStoredProcedure(String sql)
    {
        return sql.replaceAll(" ", "").contains("begin?:=");
    }

    public QueryBuildInfo processQuery(QueryBuildInfo buildInfo,QueryStructure structure) throws ExpressionParsingException
    {
        buildInfo = new QueryBuildInfo(buildInfo);
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
            param.setType(ColumnType.LONG);
            param.setValue(structure.getSkip());
            buildInfo.getExecInfo().getParams().add(param);
        }

        if (structure.getFetch() > 0)
        {
            sb.append(" FETCH NEXT ? ROWS ONLY ");

            QueryExecParam param = new QueryExecParam();
            param.setIndex(buildInfo.getExecInfo().getParams().size());
            param.setType(ColumnType.LONG);
            param.setValue(structure.getFetch());
            buildInfo.getExecInfo().getParams().add(param);
        }
    }

    private void processSelection(StringBuilder querySb,QueryBuildInfo buildInfo,QueryStructure structure) throws ExpressionParsingException
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

    protected String CreateSelectionSql(IQuerySelection selection,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        if (selection != null)
        {
            return ((IAbstractSelection)selection).createSql(dbLayer,buildInfo);
        }
        return "/*Incorrect Selection*/";
    }

    private void processFrom(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure) throws ExpressionParsingException
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

    protected String CreateFromSql(IQueryFrom from,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        if (from != null)
        {
            return ((IAbstractFrom)from).createSql(dbLayer,buildInfo);
        }
        return "/*Incorrect From*/";
    }

    private void processJoin(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure) throws ExpressionParsingException
    {
        Collection<IQueryJoin> joinList = structure.getJoinList();
        if (joinList.size() == 0)
            return;

        for (IQueryJoin join : joinList)
        {
            sb.append(" ");
            sb.append(CreateJoinSql(join,buildInfo));
        }
    }

    protected String CreateJoinSql(IQueryJoin join,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        if (join != null)
        {
            return ((IAbstractJoin)join).createSql(dbLayer,buildInfo);
        }
        return "/*Incorrect Join*/";
    }

    private void processWhere(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure) throws ExpressionParsingException
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
            sb.append(CreateWhereSql(condition,buildInfo));
            initial = false;
        }
    }

    protected String CreateWhereSql(IQueryCondition condition,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        if (condition != null)
        {
            return ((IAbstractCondition)condition).createSql(dbLayer,buildInfo);
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
            sb.append(CreateGroupSql(group,buildInfo));
            initial = false;
        }
    }

    protected String CreateGroupSql(IQueryGroup group, QueryBuildInfo buildInfo)
    {
        if (group != null)
        {
            return ((IAbstractGroup)group).createSql(dbLayer,buildInfo);
        }
        return "/*Incorrect Group*/";
    }

    private void processGroupCondition(StringBuilder sb, QueryBuildInfo buildInfo, QueryStructure structure) throws ExpressionParsingException
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
            sb.append(CreateGroupConditionSql(groupCondition,buildInfo));
            initial = false;
        }
    }

    protected String CreateGroupConditionSql(IQueryGroupCondition groupCondition,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        if (groupCondition != null)
        {
            return ((IAbstractGroupCondition)groupCondition).createSql(dbLayer,buildInfo);
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
            sb.append(CreateOrderBySql(orderBy,buildInfo));
            initial = false;
        }
    }

    protected String CreateOrderBySql(IQueryOrderBy orderBy,QueryBuildInfo buildInfo)
    {
        if (orderBy != null)
        {
            return ((IAbstractOrderBy)orderBy).createSql(dbLayer,buildInfo);
        }
        return "/*Incorrect Order*/";
    }
}
