package dbgate.ermanagement;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 1:03:00 PM
 */
public abstract class AbstractDBRelation implements IDBRelation
{
    private String attributeName;
    private String relationshipName;
    private Class relatedObjectType;
    private DBRelationColumnMapping[] tableColumnMappings;
    private ReferentialRuleType updateRule;
    private ReferentialRuleType deleteRule;
    private boolean reverseRelationship;
    private boolean nonIdentifyingRelation;

    protected AbstractDBRelation(String attributeName,String relationshipName
            , Class relatedObjectType, DBRelationColumnMapping[] tableColumnMappings)
    {
        this(attributeName,relationshipName,relatedObjectType
                ,tableColumnMappings,ReferentialRuleType.RESTRICT
                ,ReferentialRuleType.CASCADE,false,false);
    }

    protected AbstractDBRelation(String attributeName,String relationshipName
            , Class relatedObjectType, DBRelationColumnMapping[] tableColumnMappings
            , ReferentialRuleType updateRule, ReferentialRuleType deleteRule
            ,boolean reverseRelationship,boolean nonIdentifyingRelation)
    {
        this.attributeName = attributeName;
        this.relationshipName = relationshipName;
        this.relatedObjectType = relatedObjectType;
        this.tableColumnMappings = tableColumnMappings;
        this.updateRule = updateRule;
        this.deleteRule = deleteRule;
        this.reverseRelationship = reverseRelationship;
        this.nonIdentifyingRelation = nonIdentifyingRelation;
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
    public DBRelationColumnMapping[] getTableColumnMappings()
    {
        return tableColumnMappings;
    }

    @Override
    public void setTableColumnMappings(DBRelationColumnMapping[] tableColumnMappings)
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
}
