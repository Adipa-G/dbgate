package dbgate.utility;

import dbgate.EntityStatus;
import dbgate.utility.support.LeafEntity;
import dbgate.utility.support.RootEntity;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 12, 2010
 * Time: 9:26:30 AM
 */
public class StatusUtilityTests
{
    @Test
    public void statusUtility_setStatus_withMultipleLevelHierarchy_shouldSetStatus()
    {
        RootEntity rootEntity = new RootEntity();
        LeafEntity leafEntityA = new LeafEntity();
        leafEntityA.setRootEntity(rootEntity);
        rootEntity.getLeafEntities().add(leafEntityA);
        LeafEntity leafEntityB = new LeafEntity();
        leafEntityB.setRootEntity(rootEntity);
        rootEntity.getLeafEntities().add(leafEntityB);
        LeafEntity leafEntityNotNull = new LeafEntity();
        leafEntityNotNull.setRootEntity(rootEntity);
        rootEntity.setLeafEntityNotNull(leafEntityNotNull);
        rootEntity.setLeafEntityNull(null);

        StatusUtility.setStatus(rootEntity, EntityStatus.MODIFIED);

        Assert.assertEquals(rootEntity.getStatus(), EntityStatus.MODIFIED);
        Assert.assertEquals(leafEntityA.getStatus(), EntityStatus.MODIFIED);
        Assert.assertEquals(leafEntityB.getStatus(), EntityStatus.MODIFIED);
        Assert.assertEquals(leafEntityNotNull.getStatus(), EntityStatus.MODIFIED);
    }

    @Test
    public void statusUtility_isModified_withMultipleLevelHierarchy_shouldGetStatus()
    {
        RootEntity rootEntity = new RootEntity();
        LeafEntity leafEntityA = new LeafEntity();
        leafEntityA.setRootEntity(rootEntity);
        rootEntity.getLeafEntities().add(leafEntityA);
        LeafEntity leafEntityB = new LeafEntity();
        leafEntityB.setRootEntity(rootEntity);
        rootEntity.getLeafEntities().add(leafEntityB);
        LeafEntity leafEntityNotNull = new LeafEntity();
        leafEntityNotNull.setRootEntity(rootEntity);
        rootEntity.setLeafEntityNotNull(leafEntityNotNull);
        rootEntity.setLeafEntityNull(null);

        boolean unModifiedRoot = StatusUtility.isModified(rootEntity);

        rootEntity.setStatus(EntityStatus.MODIFIED);
        boolean modifiedRoot = StatusUtility.isModified(rootEntity);

        rootEntity.setStatus(EntityStatus.UNMODIFIED);
        leafEntityA.setStatus(EntityStatus.NEW);
        boolean modifiedLeafCollection = StatusUtility.isModified(rootEntity);

        leafEntityA.setStatus(EntityStatus.UNMODIFIED);
        leafEntityNotNull.setStatus(EntityStatus.DELETED);
        boolean modifiedLeafSubEntity = StatusUtility.isModified(rootEntity);

        Assert.assertFalse(unModifiedRoot);
        Assert.assertTrue(modifiedRoot);
        Assert.assertTrue(modifiedLeafCollection);
        Assert.assertTrue(modifiedLeafSubEntity);
    }

    @Test
    public void statusUtility_getImmediateChildrenAndClear_withMultipleLevelHierarchy_shouldGetChildren()
    {
        RootEntity rootEntity = new RootEntity();
        LeafEntity leafEntityA = new LeafEntity();
        leafEntityA.setRootEntity(rootEntity);
        rootEntity.getLeafEntities().add(leafEntityA);
        LeafEntity leafEntityB = new LeafEntity();
        leafEntityB.setRootEntity(rootEntity);
        rootEntity.getLeafEntities().add(leafEntityB);
        LeafEntity leafEntityNotNull = new LeafEntity();
        leafEntityNotNull.setRootEntity(rootEntity);
        rootEntity.setLeafEntityNotNull(leafEntityNotNull);
        rootEntity.setLeafEntityNull(null);

        Collection childern = StatusUtility.getImmidiateChildrenAndClear(rootEntity);

        Assert.assertTrue(childern.contains(leafEntityA));
        Assert.assertTrue(childern.contains(leafEntityB));
        Assert.assertTrue(childern.contains(leafEntityNotNull));
        Assert.assertTrue(rootEntity.getLeafEntities().size() == 0);
        Assert.assertNull(rootEntity.getLeafEntityNotNull());
    }
}
