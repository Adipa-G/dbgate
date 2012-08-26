package dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.join;

import dbgate.IRelation;
import dbgate.QueryJoinType;
import dbgate.RelationColumnMapping;
import dbgate.ermanagement.dbabstractionlayer.IDBLayer;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.AbstractExpressionProcessor;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.QueryBuildInfo;
import dbgate.ermanagement.dbabstractionlayer.datamanipulate.query.from.AbstractTypeFrom;
import dbgate.ermanagement.query.QueryJoinExpressionType;
import dbgate.ermanagement.query.expr.JoinExpr;
import dbgate.exceptions.ExpressionParsingException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/4/12
 * Time: 8:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractTypeJoin implements IAbstractJoin
{
    private Class typeFrom;
    private Class typeTo;
    private JoinExpr expr;
    private String typeToAlias;
    private QueryJoinType joinType;
    private AbstractExpressionProcessor processor;

    public AbstractTypeJoin()
    {
        joinType = QueryJoinType.INNER;
        processor = new AbstractExpressionProcessor();
    }

    public Class getTypeFrom()
    {
        return typeFrom;
    }

    public void setTypeFrom(Class typeFrom)
    {
        this.typeFrom = typeFrom;
    }

    public Class getTypeTo()
    {
        return typeTo;
    }

    public void setTypeTo(Class typeTo)
    {
        this.typeTo = typeTo;
    }

    public JoinExpr getExpr()
    {
        return expr;
    }

    public void setExpr(JoinExpr expr)
    {
        this.expr = expr;
    }

    public String getTypeToAlias()
    {
        return typeToAlias;
    }

    public void setTypeToAlias(String typeToAlias)
    {
        this.typeToAlias = typeToAlias;
    }

    public QueryJoinType getJoinType()
    {
        return joinType;
    }

    public void setJoinType(QueryJoinType joinType)
    {
        this.joinType = joinType;
    }

    @Override
    public String createSql(IDBLayer dbLayer,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        if (expr == null)
        {
            createJoinExpressionForDefinedRelation(buildInfo);
        }
        return createSqlForExpression(dbLayer,buildInfo);
    }

    private void createJoinExpressionForDefinedRelation(QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        String typeFromAlias = buildInfo.getAlias(typeFrom);
        IRelation relation = processor.getRelation(typeFrom,typeTo);
        if (relation == null)
        {
            relation = processor.getRelation(typeTo,typeFrom);
        }

        if (relation != null)
        {
            expr = JoinExpr.build();
            RelationColumnMapping[] tableColumnMappings = relation.getTableColumnMappings();
            for (int i = 0, tableColumnMappingsLength = tableColumnMappings.length; i < tableColumnMappingsLength; i++)
            {
                RelationColumnMapping mapping = tableColumnMappings[i];
                if (i > 0)
                {
                    expr.and();
                }
                expr.field(typeFrom, typeFromAlias, mapping.getFromField());
                expr.eq();
                expr.field(typeTo, typeToAlias, mapping.getToField());
            }
        }
    }

    private String createSqlForExpression(IDBLayer dbLayer,QueryBuildInfo buildInfo) throws ExpressionParsingException
    {
        AbstractTypeFrom from = new AbstractTypeFrom();
        from.setType(typeTo);
        from.setAlias(typeToAlias);

        StringBuilder sb = new StringBuilder();
        appendJoinTypeSql(sb);
        sb.append(from.createSql(dbLayer,buildInfo));
        sb.append(" ON ");
        processor.process(sb,expr.getRootSegment(),buildInfo,dbLayer);
        return sb.toString();
    }

    private void appendJoinTypeSql(StringBuilder sb)
    {
        switch (joinType)
        {
            case INNER:
                sb.append("INNER JOIN "); break;
            case LEFT:
                sb.append("LEFT JOIN "); break;
            case RIGHT:
                sb.append("RIGHT JOIN "); break;
            case FULL:
                sb.append("FULL JOIN ");break;
        }
    }

    @Override
    public QueryJoinExpressionType getJoinExpressionType()
    {
        return QueryJoinExpressionType.TYPE;
    }
}
