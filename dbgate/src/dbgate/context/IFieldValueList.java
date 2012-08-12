package dbgate.context;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 2:38:07 PM
 */
public interface IFieldValueList
{
    Collection<EntityFieldValue> getFieldValues();

    EntityFieldValue getFieldValue(String attributeName);
}