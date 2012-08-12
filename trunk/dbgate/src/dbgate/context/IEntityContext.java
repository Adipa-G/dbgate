package dbgate.context;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Mar 26, 2011
 * Time: 11:04:07 AM
 */
public interface IEntityContext
{
    IChangeTracker getChangeTracker();

    IERSession getERSession();

    void setERSession(IERSession erSession);
}
