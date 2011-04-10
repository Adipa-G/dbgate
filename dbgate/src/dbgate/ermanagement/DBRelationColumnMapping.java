package dbgate.ermanagement;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 26, 2010
 * Time: 5:19:58 PM
 */
public class DBRelationColumnMapping
{
    private String fromField;
    private String toField;

    public DBRelationColumnMapping(String fromField, String toField)
    {
        this.fromField = fromField;
        this.toField = toField;
    }

    public String getFromField()
    {
        return fromField;
    }

    public void setFromField(String fromField)
    {
        this.fromField = fromField;
    }

    public String getToField()
    {
        return toField;
    }

    public void setToField(String toField)
    {
        this.toField = toField;
    }
}
