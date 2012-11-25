package dbgate.persist.support.inheritancetest;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 4:23:25 PM
 */
public interface IInheritanceTestSubEntityB extends IInheritanceTestSuperEntity
{
    String getNameB();

    void setNameB(String nameB);
}