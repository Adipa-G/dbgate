package dbgate.lazy;

import dbgate.IReadOnlyEntity;
import dbgate.IRelation;
import dbgate.ITransaction;
import dbgate.ITransactionFactory;
import dbgate.caches.CacheManager;
import dbgate.caches.impl.EntityInfo;
import dbgate.ermanagement.ermapper.RetrievalOperationLayer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

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
    private ITransactionFactory transactionFactory;
    private ITransaction transaction;

    public ChildLoadInterceptor(RetrievalOperationLayer retrievalOperationLayer, IReadOnlyEntity parentRoEntity,
                                Class applicableParentType,ITransaction transaction, IRelation relation)
    {
        this.retrievalOperationLayer = retrievalOperationLayer;
        this.parentRoEntity = parentRoEntity;
        this.applicableParentType = applicableParentType;
        this.transaction = transaction;
        this.transactionFactory = transaction.getFactory();
        this.relation = relation;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable
    {
        Object objectToInvoke = o;
        if (!intercepted)
        {
            intercepted = true;
            boolean newTransaction = false;
            try
            {
                if (transaction.isClosed())
                {
                    transaction = transactionFactory.createTransaction();
                    newTransaction = true;
                }
                retrievalOperationLayer.loadChildrenFromRelation(parentRoEntity,applicableParentType,transaction,relation,true);
            }
            finally
            {
                if (newTransaction)
                {
                    transaction.close();
                    transaction = null;
                }
            }

            EntityInfo entityInfo = CacheManager.getEntityInfo(parentRoEntity);
            Method getter = entityInfo.getGetter(relation.getAttributeName());
            objectToInvoke = getter.invoke(parentRoEntity);
        }

        return method.invoke(objectToInvoke,objects);
    }
}
