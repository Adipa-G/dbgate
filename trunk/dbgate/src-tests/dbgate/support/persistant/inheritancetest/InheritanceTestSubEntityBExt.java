package dbgate.support.persistant.inheritancetest;

import dbgate.ITransaction;
import dbgate.exceptions.PersistException;
import dbgate.exceptions.RetrievalException;

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

    public void persist(ITransaction tx) throws PersistException
    {
        tx.getDbGate().save(this,tx);
    }

    public void retrieve(ResultSet rs, ITransaction tx) throws RetrievalException
    {
        tx.getDbGate().load(this,rs,tx);
    }
}