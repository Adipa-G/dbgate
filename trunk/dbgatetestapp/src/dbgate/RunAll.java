package dbgate;

import dbgate.complexexample.ComplexExample;
import dbgate.inheritanceexample.InheritanceExample;
import dbgate.one2manyexample.One2ManyExample;
import dbgate.one2oneexample.One2OneExample;
import dbgate.simpleexample.SimpleExample;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/1/12
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunAll
{
    public static void main(String[] args)
    {
        SimpleExample.main(args);
        One2ManyExample.main(args);
        One2OneExample.main(args);
        InheritanceExample.main(args);
        ComplexExample.main(args);
    }
}
