package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jul 5, 2008
 * Time: 1:42:05 PM
 */
public interface IColumn extends IField
{
    ColumnType getColumnType();

    void setColumnType(ColumnType type);

    int getSize();

    void setSize(int type);

    String getColumnName();

    void setColumnName(String dbColumn);

    boolean isKey();

    void setKey(boolean key);

    //to avoid read column twice to prevent errors in un supported databases
    boolean isSubClassCommonColumn();

    void setSubClassCommonColumn(boolean value);

    void setNullable(boolean key);

    boolean isNullable();

    boolean isReadFromSequence();

    void setReadFromSequence(boolean readFromSequence);

    ISequenceGenerator getSequenceGenerator();

    void setSequenceGenerator(ISequenceGenerator generator);
}
