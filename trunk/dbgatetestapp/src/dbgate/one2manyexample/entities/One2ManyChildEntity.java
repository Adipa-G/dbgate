package dbgate.one2manyexample.entities;

import dbgate.ermanagement.DefaultServerDBClass;

/**
 * Date: Mar 30, 2011
 * Time: 8:51:25 PM
 */

public abstract class One2ManyChildEntity extends DefaultServerDBClass
{
    public abstract String getName();

    public abstract void setName(String name);
}