package dbgate;

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
    ForeignKeyFieldMapping[] fieldMappings();
    ReferentialRuleType updateRule() default ReferentialRuleType.RESTRICT;
    ReferentialRuleType deleteRule() default ReferentialRuleType.CASCADE;
    Class relatedObjectType() default Object.class;
    boolean nullable() default false;
    boolean reverseRelation() default false;
    boolean nonIdentifyingRelation() default false;
    FetchStrategy fetchStrategy() default FetchStrategy.DEFAULT;
}
