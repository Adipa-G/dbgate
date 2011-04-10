package dbgate.ermanagement.caches;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.exceptions.TableCacheMissException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 12:57:40 PM
 */
public interface ITableCache
{
    String getTableName(Class type) throws TableCacheMissException;

    void register(Class type,String tableName);

    void register(Class type,ServerRODBClass serverRODBClass);

    void clear();
}
