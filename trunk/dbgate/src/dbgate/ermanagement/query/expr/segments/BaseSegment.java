package dbgate.ermanagement.query.expr.segments;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 8/3/12
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseSegment implements ISegment
{
    protected ISegment parent;
    protected ISegment active;

    public ISegment getParent()
    {
        return parent;
    }

    public void setParent(ISegment parent)
    {
        this.parent = parent;
    }

    public ISegment getActive()
    {
        return active;
    }

    public void setActive(ISegment active)
    {
        this.active = active;
    }
}
