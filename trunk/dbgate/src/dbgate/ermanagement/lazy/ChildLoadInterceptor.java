package dbgate.ermanagement.lazy;

import dbgate.ServerRODBClass;
import dbgate.dbutility.DBConnector;
import dbgate.dbutility.DBMgmtUtility;
import dbgate.ermanagement.IDBRelation;
import dbgate.ermanagement.caches.CacheManager;
import dbgate.ermanagement.caches.impl.EntityInfo;
import dbgate.ermanagement.impl.ERDataRetrievalManager;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 4/16/12
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChildLoadInterceptor implements MethodInterceptor
{
    private ERDataRetrievalManager erDataRetrievalManager;
    private ServerRODBClass parentRoEntity;
    private Class applicableParentType;
    private IDBRelation relation;
    private boolean intercepted;
    private Connection connection;

    public ChildLoadInterceptor(ERDataRetrievalManager erDataRetrievalManager, ServerRODBClass parentRoEntity,
                                Class applicableParentType,Connection connection, IDBRelation relation)
    {
        this.erDataRetrievalManager = erDataRetrievalManager;
        this.parentRoEntity = parentRoEntity;
        this.applicableParentType = applicableParentType;
        this.connection = connection;
        this.relation = relation;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable
    {
        Object objectToInvoke = o;
        if (!intercepted)
        {
            intercepted = true;
            boolean newConnection = false;
            try
            {
                if (connection == null || connection.isClosed())
                {
                    connection = DBConnector.getSharedInstance().getConnection();
                    newConnection = true;
                }
                erDataRetrievalManager.loadChildrenFromRelation(parentRoEntity,applicableParentType,connection,relation,true);
            }
            finally
            {
                if (newConnection)
                {
                    DBMgmtUtility.close(connection);
                    connection = null;
                }
            }

            EntityInfo entityInfo = CacheManager.getEntityInfo(parentRoEntity);
            Method getter = entityInfo.getGetter(relation.getAttributeName());
            objectToInvoke = getter.invoke(parentRoEntity);
        }

        return method.invoke(objectToInvoke,objects);
    }
}
