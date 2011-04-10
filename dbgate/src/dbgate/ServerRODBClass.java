package dbgate;

import dbgate.ermanagement.context.IEntityContext;
import dbgate.ermanagement.exceptions.RetrievalException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Nov 4, 2006
 * Time: 8:15:19 PM
 * ----------------------------------------
 */

/**
 * read only class
 */

public interface ServerRODBClass extends IRODBClass, Serializable
{
    void retrieve(ResultSet rs, Connection con) throws RetrievalException;

    IEntityContext getContext();
}
