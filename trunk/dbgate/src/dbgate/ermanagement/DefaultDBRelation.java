package dbgate.ermanagement;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 1:06:27 PM
 */
public class DefaultDBRelation extends AbstractDBRelation
{
    public DefaultDBRelation(String attributeName, String relationshipName
            , Class relatedObjectType, DBRelationColumnMapping[] tableColumnMappings)
    {
        super(attributeName, relationshipName
                , relatedObjectType, tableColumnMappings);
    }

    public DefaultDBRelation(String attributeName, String relationshipName
            ,Class relatedObjectType, DBRelationColumnMapping[] tableColumnMappings
            , ReferentialRuleType updateRule, ReferentialRuleType deleteRule
            , boolean reverseRelationship, boolean nonIdentifyingRelation)
    {
        super(attributeName, relationshipName, relatedObjectType
                , tableColumnMappings, updateRule, deleteRule
                , reverseRelationship,nonIdentifyingRelation);
    }
}
