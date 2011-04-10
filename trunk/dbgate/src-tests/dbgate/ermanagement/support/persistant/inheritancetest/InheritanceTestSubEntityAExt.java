package dbgate.ermanagement.support.persistant.inheritancetest;

import dbgate.ermanagement.exceptions.PersistException;
import dbgate.ermanagement.exceptions.RetrievalException;
import dbgate.ermanagement.impl.ERLayer;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 12:23:11 PM
 */
public class InheritanceTestSubEntityAExt extends InheritanceTestSuperEntityExt implements IInheritanceTestSubEntityA
{
    private String nameA;

    public String getNameA()
    {
        return nameA;
    }

    public void setNameA(String nameA)
    {
        this.nameA = nameA;
    }

    public void persist(Connection con) throws PersistException
    {
        ERLayer.getSharedInstance().save(this,con);
    }

    public void retrieve(ResultSet rs, Connection con) throws RetrievalException
    {
        ERLayer.getSharedInstance().load(this,rs,con);
    }
}