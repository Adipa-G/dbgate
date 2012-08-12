package dbgate;

import dbgate.context.IEntityContext;
import dbgate.exceptions.RetrievalException;

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

public interface IReadOnlyEntity extends IReadOnlyClientEntity, Serializable
{
    void retrieve(ResultSet rs, Connection con) throws RetrievalException;

    IEntityContext getContext();
}
