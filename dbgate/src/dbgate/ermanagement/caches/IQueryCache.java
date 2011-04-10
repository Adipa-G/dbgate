package dbgate.ermanagement.caches;

import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.exceptions.FieldCacheMissException;
import dbgate.ermanagement.exceptions.QueryBuildingException;
import dbgate.ermanagement.exceptions.TableCacheMissException;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 6:17:27 PM
 */
public interface IQueryCache
{
    String getLoadQuery(Class entityType) throws FieldCacheMissException, QueryBuildingException, TableCacheMissException;

    String getInsertQuery(Class entityType) throws FieldCacheMissException, QueryBuildingException, TableCacheMissException;

    String getUpdateQuery(Class entityType) throws FieldCacheMissException, QueryBuildingException, TableCacheMissException;

    String getDeleteQuery(Class entityType) throws FieldCacheMissException, QueryBuildingException, TableCacheMissException;

    String getRelationObjectLoad(Class entityType, IDBRelation relation) throws QueryBuildingException, TableCacheMissException, FieldCacheMissException;

    void clear();
}
