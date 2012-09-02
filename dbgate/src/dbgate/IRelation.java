package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 12:50:23 PM
 */
public interface IRelation extends IField
{
    String getRelationshipName();

    void setRelationshipName(String relationshipName);

    Class getRelatedObjectType();

    void setRelatedObjectType(Class relatedObjectType);

    RelationColumnMapping[] getTableColumnMappings();

    void setTableColumnMappings(RelationColumnMapping[] tableColumnMappings);

    ReferentialRuleType getUpdateRule();

    void setUpdateRule(ReferentialRuleType updateRule);

    ReferentialRuleType getDeleteRule();

    void setDeleteRule(ReferentialRuleType deleteRule);

    boolean isReverseRelationship();

    void setReverseRelationship(boolean reverse);

    boolean isNonIdentifyingRelation();

    void setNonIdentifyingRelation(boolean nonIdentifying);

    boolean isLazy();

    void setLazy(boolean lazy);

    boolean isNullable();

    void setNullable(boolean nullable);

    IRelation clone();
}
