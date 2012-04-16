package dbgate.ermanagement;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 7:01:11 PM
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ForeignKeyInfo
{
    String name();
    ForeignKeyColumnMapping[] columnMappings();
    ReferentialRuleType updateRule() default ReferentialRuleType.RESTRICT;
    ReferentialRuleType deleteRule() default ReferentialRuleType.CASCADE;
    Class relatedObjectType() default Object.class;
    boolean reverseRelation() default false;
    boolean nonIdentifyingRelation() default false;
    boolean lazy() default false;
}
