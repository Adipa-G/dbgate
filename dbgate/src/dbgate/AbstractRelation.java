package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 1:03:00 PM
 */
public abstract class AbstractRelation implements IRelation
{
    private String attributeName;
    private String relationshipName;
    private Class sourceObjectType;
    private Class relatedObjectType;
    private RelationColumnMapping[] tableColumnMappings;
    private ReferentialRuleType updateRule;
    private ReferentialRuleType deleteRule;
    private boolean reverseRelationship;
    private boolean nonIdentifyingRelation;
    private FetchStrategy fetchStrategy;
    private boolean nullable;

    protected AbstractRelation(String attributeName,
                               String relationshipName,
                               Class sourceObjectType,
                               Class relatedObjectType,
                               RelationColumnMapping[] tableColumnMappings)
    {
        this(attributeName,
             relationshipName,
             sourceObjectType,
             relatedObjectType,
             tableColumnMappings,ReferentialRuleType.RESTRICT,
             ReferentialRuleType.CASCADE,
             false,
             false,
             FetchStrategy.DEFAULT,
             false);
    }

    protected AbstractRelation(String attributeName,
                               String relationshipName,
                               Class sourceObjectType,
                               Class relatedObjectType,
                               RelationColumnMapping[] tableColumnMappings,
                               ReferentialRuleType updateRule,
                               ReferentialRuleType deleteRule,
                               boolean reverseRelationship,
                               boolean nonIdentifyingRelation,
                               FetchStrategy fetchStrategy,
                               boolean nullable)
    {
        this.attributeName = attributeName;
        this.relationshipName = relationshipName;
        this.sourceObjectType = sourceObjectType;
        this.relatedObjectType = relatedObjectType;
        this.tableColumnMappings = tableColumnMappings;
        this.updateRule = updateRule;
        this.deleteRule = deleteRule;
        this.reverseRelationship = reverseRelationship;
        this.nonIdentifyingRelation = nonIdentifyingRelation;
        this.fetchStrategy = fetchStrategy;
        this.nullable = nullable;
    }

    @Override
    public String getAttributeName()
    {
        return attributeName;
    }

    @Override
    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }

    @Override
    public String getRelationshipName()
    {
        return relationshipName;
    }

    @Override
    public void setRelationshipName(String relationshipName)
    {
        this.relationshipName = relationshipName;
    }

    @Override
    public Class getSourceObjectType()
    {
        return sourceObjectType;
    }

    @Override
    public void setSourceObjectType(Class sourceObjectType)
    {
        this.sourceObjectType = sourceObjectType;
    }

    @Override
    public Class getRelatedObjectType()
    {
        return relatedObjectType;
    }

    @Override
    public void setRelatedObjectType(Class relatedObjectType)
    {
        this.relatedObjectType = relatedObjectType;
    }

    @Override
    public RelationColumnMapping[] getTableColumnMappings()
    {
        return tableColumnMappings;
    }

    @Override
    public void setTableColumnMappings(RelationColumnMapping[] tableColumnMappings)
    {
        this.tableColumnMappings = tableColumnMappings;
    }

    @Override
    public ReferentialRuleType getUpdateRule()
    {
        return updateRule;
    }

    @Override
    public void setUpdateRule(ReferentialRuleType updateRule)
    {
        this.updateRule = updateRule;
    }

    @Override
    public ReferentialRuleType getDeleteRule()
    {
        return deleteRule;
    }

    @Override
    public void setDeleteRule(ReferentialRuleType deleteRule)
    {
        this.deleteRule = deleteRule;
    }

    @Override
    public boolean isReverseRelationship()
    {
        return reverseRelationship;
    }

    @Override
    public void setReverseRelationship(boolean reverseRelationship)
    {
        this.reverseRelationship = reverseRelationship;
    }

    @Override
    public boolean isNonIdentifyingRelation()
    {
        return nonIdentifyingRelation;
    }

    @Override
    public void setNonIdentifyingRelation(boolean nonIdentifying)
    {
        this.nonIdentifyingRelation = nonIdentifying;
    }

    @Override
    public FetchStrategy getFetchStrategy()
    {
        return fetchStrategy;
    }

    @Override
    public void setFetchStrategy(FetchStrategy strategy)
    {
        this.fetchStrategy = strategy;
    }

    @Override
    public boolean isNullable()
    {
        return this.nullable;
    }

    @Override
    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }

    @Override
    public IRelation clone()
    {
        try
        {
            IRelation relation = (IRelation) super.clone();
            RelationColumnMapping[] sourceColumnMappings = relation.getTableColumnMappings();
            RelationColumnMapping[] destColumnMappings = new RelationColumnMapping[sourceColumnMappings.length];

            for (int i = 0; i < sourceColumnMappings.length; i++)
            {
                RelationColumnMapping columnMapping = sourceColumnMappings[i];
                destColumnMappings[i] = columnMapping.clone();
            }

            relation.setTableColumnMappings(destColumnMappings);
            return relation;
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
