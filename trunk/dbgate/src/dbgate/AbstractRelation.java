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
    private Class relatedObjectType;
    private RelationColumnMapping[] tableColumnMappings;
    private ReferentialRuleType updateRule;
    private ReferentialRuleType deleteRule;
    private boolean reverseRelationship;
    private boolean nonIdentifyingRelation;
    private boolean lazy;

    protected AbstractRelation(String attributeName, String relationshipName
            , Class relatedObjectType, RelationColumnMapping[] tableColumnMappings)
    {
        this(attributeName,relationshipName,relatedObjectType
                ,tableColumnMappings,ReferentialRuleType.RESTRICT
                ,ReferentialRuleType.CASCADE,false,false,false);
    }

    protected AbstractRelation(String attributeName, String relationshipName
            , Class relatedObjectType, RelationColumnMapping[] tableColumnMappings
            , ReferentialRuleType updateRule, ReferentialRuleType deleteRule
            , boolean reverseRelationship, boolean nonIdentifyingRelation, boolean lazy)
    {
        this.attributeName = attributeName;
        this.relationshipName = relationshipName;
        this.relatedObjectType = relatedObjectType;
        this.tableColumnMappings = tableColumnMappings;
        this.updateRule = updateRule;
        this.deleteRule = deleteRule;
        this.reverseRelationship = reverseRelationship;
        this.nonIdentifyingRelation = nonIdentifyingRelation;
        this.lazy = lazy;
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
    public boolean isLazy()
    {
        return lazy;
    }

    @Override
    public void setLazy(boolean lazy)
    {
        this.lazy = lazy;
    }

    @Override
    public IRelation clone()
    {
        try
        {
            return (IRelation) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
