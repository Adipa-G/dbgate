package dbgate.context;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:03:10 AM
 */
public interface IChangeTracker
{
    Collection<EntityFieldValue> getFields();

    Collection<ITypeFieldValueList> getChildEntityKeys();

    EntityFieldValue getFieldValue(String attributeName);

    boolean isValid();
}
