package dbgate.support.persistant.inheritancetest;

import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;
import dbgate.ermanagement.ermapper.DbGate;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
public class InheritanceTestSubEntityBExt extends InheritanceTestSuperEntityExt implements IInheritanceTestSubEntityB
{
    private String nameB;

    public String getNameB()
    {
        return nameB;
    }

    public void setNameB(String nameB)
    {
        this.nameB = nameB;
    }

    public void persist(Connection con) throws PersistException
    {
        DbGate.getSharedInstance().save(this,con);
    }

    public void retrieve(ResultSet rs, Connection con) throws RetrievalException
    {
        DbGate.getSharedInstance().load(this,rs,con);
    }
}