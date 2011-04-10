package dbgate.ermanagement.impl.dbabstractionlayer.metamanipulate.mappings;

import dbgate.ermanagement.ReferentialRuleType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 7:27:00 PM
 */
public class ReferentialRuleTypeMapItem
{
    private ReferentialRuleType ruleType;
    private String ruleName;

    public ReferentialRuleTypeMapItem()
    {
    }

    public ReferentialRuleTypeMapItem(ReferentialRuleType ruleType, String ruleName)
    {
        this.ruleType = ruleType;
        this.ruleName = ruleName;
    }

    public ReferentialRuleType getRuleType()
    {
        return ruleType;
    }

    public void setRuleType(ReferentialRuleType ruleType)
    {
        this.ruleType = ruleType;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }
}
