package dbgate.ermanagement.context;

import dbgate.ServerRODBClass;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:05:32 AM
 */
public interface IERSession
{
    Collection<IEntityFieldValueList> getProcessedObjects();

    boolean isProcessed(ITypeFieldValueList typeKeyFieldList);

    ServerRODBClass getProcessed(ITypeFieldValueList typeKeyFieldList);

    void checkAndAddEntityList(IEntityFieldValueList entityKeyFieldList);
}
