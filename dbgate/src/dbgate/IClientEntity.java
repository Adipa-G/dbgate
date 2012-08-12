package dbgate;

/**
  writable interface
 */
public interface IClientEntity extends IReadOnlyClientEntity
{
    EntityStatus getStatus();

    void setStatus(EntityStatus status);
}