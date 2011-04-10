package dbgate;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jun 25, 2006
 * Time: 1:26:23 PM
 * ----------------------------------------
 */
public class DateWrapper implements Cloneable,Comparable, Serializable
{
    protected Date date;

    public DateWrapper()
    {
        setFromDate(new Date());
    }

    public DateWrapper(int year, int month, int date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DATE,date);

        this.date = cal.getTime();
    }

    public DateWrapper(Date date)
    {
        setFromDate(date);
    }

    public DateWrapper(java.sql.Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        setFromDate(cal.getTime());
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        setFromDate(date);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof DateWrapper)
        {
            DateWrapper dateWrapper = (DateWrapper) obj;
            return date.equals(dateWrapper.getDate());
        }
        else
        {
            return false;
        }
    }

    public int compareTo(Object o)
    {
        if (o instanceof DateWrapper)
        {
            DateWrapper date = (DateWrapper)o;
            date.getDate().compareTo(this.date);
        }
        return -1;
    }


    public java.sql.Date _getSQLDate()
    {
        return new java.sql.Date(this.date.getTime());
    }

    public java.sql.Timestamp _getSQLTimeStamp()
    {
        return new java.sql.Timestamp(this.date.getTime());
    }

    public DateWrapper clone()
    {
        try
        {
            return (DateWrapper) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected void setFromDate(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);

        cal = Calendar.getInstance();
        cal.setTimeInMillis(0);

        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DATE,day);

        this.date = cal.getTime();
    }
}
