package dbgate;

/**
  writable interface
 */
public interface IDBClass extends IRODBClass
{
    DBClassStatus getStatus();

    void setStatus(DBClassStatus status);
}