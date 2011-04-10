package dbgate.ermanagement;

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
public @interface DBTableInfo
{
    String tableName();
}
