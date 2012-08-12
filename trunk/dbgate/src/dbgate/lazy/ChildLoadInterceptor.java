package dbgate.lazy;

import dbgate.DBConnector;
import dbgate.IReadOnlyEntity;
import dbgate.IRelation;
import dbgate.utility.DBMgtUtility;
import dbgate.caches.CacheManager;
import dbgate.caches.impl.EntityInfo;
import dbgate.ermanagement.impl.RetrievalOperationLayer;
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
    private RetrievalOperationLayer retrievalOperationLayer;
    private IReadOnlyEntity parentRoEntity;
    private Class applicableParentType;
    private IRelation relation;
    private boolean intercepted;
    private Connection connection;

    public ChildLoadInterceptor(RetrievalOperationLayer retrievalOperationLayer, IReadOnlyEntity parentRoEntity,
                                Class applicableParentType,Connection connection, IRelation relation)
    {
        this.retrievalOperationLayer = retrievalOperationLayer;
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
                retrievalOperationLayer.loadChildrenFromRelation(parentRoEntity,applicableParentType,connection,relation,true);
            }
            finally
            {
                if (newConnection)
                {
                    DBMgtUtility.close(connection);
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
