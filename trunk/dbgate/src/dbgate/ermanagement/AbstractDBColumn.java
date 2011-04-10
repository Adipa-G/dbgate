package dbgate.ermanagement;

import dbgate.DBColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Jul 5, 2008
 * Time: 1:54:14 PM
 */
public abstract class AbstractDBColumn implements IDBColumn
{
    private String attributeName;
    private String columnName;
    private boolean key;
    private boolean nullable;
    private boolean subClassCommonColumn;
    private DBColumnType columnType;
    private int size;
    private boolean readFromSequence;
    private ISequenceGenerator sequenceGenerator;

    private static String predictColumnName(String attributeName)
    {
        boolean previousCaps = false;
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = attributeName.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            char aChar = chars[i];
            if (Character.isUpperCase(aChar))
            {
                if (!previousCaps)
                {
                    stringBuilder.append("_");
                }
                previousCaps = true;
            }
            else
            {
                previousCaps = false;
            }
            stringBuilder.append(aChar);
        }
        return stringBuilder.toString();
    }

    protected AbstractDBColumn(String attributeName, DBColumnType type)
    {
        this(attributeName,predictColumnName(attributeName),false,type,false,null);
    }

    protected AbstractDBColumn(String attributeName, DBColumnType type,boolean nullable)
    {
        this(attributeName,predictColumnName(attributeName),false,nullable,type,20,false,null);
    }

    protected AbstractDBColumn(String attributeName, boolean key, DBColumnType type)
    {
        this(attributeName,predictColumnName(attributeName),key,type,false,null);
    }

    protected AbstractDBColumn(String attributeName, boolean key,boolean nullable, DBColumnType type)
    {
        this(attributeName,predictColumnName(attributeName),key,nullable,type,20,false,null);
    }

    protected AbstractDBColumn(String attributeName, String columnName, boolean key
            , DBColumnType type, boolean readFromSequence,ISequenceGenerator generator)
    {
        this(attributeName,columnName,key,false,type,20,readFromSequence,generator);
    }

    protected AbstractDBColumn(String attributeName, String columnName, boolean key
            ,boolean nullable, DBColumnType type,int size, boolean readFromSequence,ISequenceGenerator generator)
    {
        this.attributeName = attributeName;
        this.columnName = columnName;
        this.key = key;
        this.nullable = nullable;
        this.columnType = type;
        this.size = size;
        this.readFromSequence = readFromSequence;
        this.sequenceGenerator = generator;
    }

    @Override
    public DBColumnType getColumnType()
    {
        return columnType;
    }

    @Override
    public void setColumnType(DBColumnType type)
    {
        this.columnType = type;
    }

    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public void setSize(int size)
    {
        this.size = size;
    }

    @Override
    public String getAttributeName()
    {
        return attributeName;
    }

    @Override
    public void setAttributeName(String fieldId)
    {
        this.attributeName = fieldId;
    }

    @Override
    public String getColumnName()
    {
        return columnName;
    }

    @Override
    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }

    @Override
    public boolean isKey()
    {
        return key;
    }

    @Override
    public void setKey(boolean key)
    {
        this.key = key;
    }

    @Override
    public boolean isNullable()
    {
        return nullable;
    }

    @Override
    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }

    @Override
    public boolean isSubClassCommonColumn()
    {
        return subClassCommonColumn;
    }

    @Override
    public void setSubClassCommonColumn(boolean subClassCommonColumn)
    {
        this.subClassCommonColumn = subClassCommonColumn;
    }

    @Override
    public boolean isReadFromSequence()
    {
        return readFromSequence;
    }

    @Override
    public void setReadFromSequence(boolean readFromSequence)
    {
        this.readFromSequence = readFromSequence;
    }

    @Override
    public ISequenceGenerator getSequenceGenerator()
    {
        return sequenceGenerator;
    }

    @Override
    public void setSequenceGenerator(ISequenceGenerator sequenceGenerator)
    {
        this.sequenceGenerator = sequenceGenerator;
    }
}
