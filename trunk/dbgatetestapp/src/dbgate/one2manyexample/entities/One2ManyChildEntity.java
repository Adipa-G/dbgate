package dbgate.one2manyexample.entities;


import dbgate.DefaultEntity;
import docgenerate.WikiCodeBlock;

/**
 * Date: Mar 30, 2011
 * Time: 8:51:25 PM
 */

@WikiCodeBlock(id = "one_2_many_example_child_entity")
public abstract class One2ManyChildEntity extends DefaultEntity
{
    public abstract String getName();

    public abstract void setName(String name);
}
