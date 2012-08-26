package dbgate;

import dbgate.exceptions.PersistException;

/**
  writable interface
 */
public interface IEntity extends IReadOnlyEntity, IClientEntity
{
    void persist( ITransaction tx ) throws PersistException;
}
