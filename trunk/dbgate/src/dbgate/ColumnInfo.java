package dbgate;

import dbgate.ColumnType;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 5:07:15 PM
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ColumnInfo
{
    ColumnType columnType();
    String columnName() default "";
    boolean key() default false;
    boolean nullable() default false;
    boolean subClassCommonColumn() default false;
    int size() default 20;
    boolean readFromSequence() default false;
    String sequenceGeneratorClassName() default "";
}
