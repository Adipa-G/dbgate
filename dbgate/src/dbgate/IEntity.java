package dbgate;

import dbgate.ermanagement.exceptions.PersistException;

import java.sql.Connection;

/**
  writable interface
 */
public interface IEntity extends IReadOnlyEntity, IClientEntity
{
    void persist( Connection con ) throws PersistException;
}
