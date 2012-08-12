package dbgate.ermanagement.context;

import dbgate.IReadOnlyEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 2:38:07 PM
 */
public interface IEntityFieldValueList extends ITypeFieldValueList
{
    IReadOnlyEntity getEntity();
}