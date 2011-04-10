package dbgate;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 6, 2006
 * Time: 6:21:49 PM
 * ----------------------------------------
 */
public class TimeStampWrapper extends DateWrapper implements Cloneable
{
    public TimeStampWrapper()
    {
        setFromDate(new Date());
    }

    public TimeStampWrapper(Date date)
    {
        setFromDate(date);
    }

    public TimeStampWrapper(long millis)
    {
        this(new Date(millis));
    }

    public TimeStampWrapper(DateWrapper dateWrapper)
    {
        this(dateWrapper.getDate());
    }

    public TimeStampWrapper(java.sql.Timestamp date)
    {
        setFromDate(date);
    }

    public TimeStampWrapper clone()
    {
        return (TimeStampWrapper) super.clone();
    }

    protected void setFromDate(Date date)
    {
        super.setFromDate(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(this.date);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        cal = Calendar.getInstance();
        cal.setTimeInMillis(this.date.getTime());

        cal.set(Calendar.HOUR,hour);
        cal.set(Calendar.MINUTE,minute);
        cal.set(Calendar.SECOND,second);

        this.date = cal.getTime();
    }
}
