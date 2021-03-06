package dbgate;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 4:20:07 PM
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKeyFieldMapping
{
    String fromField();
    String toField();
}
