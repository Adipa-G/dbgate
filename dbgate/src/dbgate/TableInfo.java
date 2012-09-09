package dbgate;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 12:52:11 PM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableInfo
{
    String tableName();
    UpdateStrategy updateStrategy() default UpdateStrategy.DEFAULT;
    VerifyOnWriteStrategy verifyOnWriteStrategy() default VerifyOnWriteStrategy.DEFAULT;
    DirtyCheckStrategy dirtyCheckStrategy() default DirtyCheckStrategy.DEFAULT;
}
