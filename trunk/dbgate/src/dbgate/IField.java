package dbgate;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Sep 22, 2010
 * Time: 12:44:02 PM
 */
public interface IField extends Cloneable
{
    String getAttributeName();

    void setAttributeName(String attributeName);

    IField clone();
}
