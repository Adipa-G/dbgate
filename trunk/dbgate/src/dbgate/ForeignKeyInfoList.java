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
public @interface ForeignKeyInfoList
{
    public abstract ForeignKeyInfo[] infoList();
}