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

    void addFields(Collection<EntityFieldValue> fields);

    Collection<ITypeFieldValueList> getChildEntityKeys();

    void addChildEntityKey(ITypeFieldValueList childEntityKey);

    EntityFieldValue getFieldValue(String attributeName);

    boolean isValid();
}
