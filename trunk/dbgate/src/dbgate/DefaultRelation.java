package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 1:06:27 PM
 */
public class DefaultRelation extends AbstractRelation
{
    public DefaultRelation(String attributeName, String relationshipName
            , Class relatedObjectType, RelationColumnMapping[] tableColumnMappings)
    {
        super(attributeName, relationshipName
                , relatedObjectType, tableColumnMappings);
    }

    public DefaultRelation(String attributeName, String relationshipName
            , Class relatedObjectType, RelationColumnMapping[] tableColumnMappings
            , ReferentialRuleType updateRule, ReferentialRuleType deleteRule
            , boolean reverseRelationship, boolean nonIdentifyingRelation, FetchStrategy strategy,boolean nullable)
    {
        super(attributeName, relationshipName, relatedObjectType
                , tableColumnMappings, updateRule, deleteRule
                , reverseRelationship,nonIdentifyingRelation,strategy,nullable);
    }
}
