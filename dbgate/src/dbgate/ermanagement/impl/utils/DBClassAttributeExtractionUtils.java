package dbgate.ermanagement.impl.utils;

import dbgate.ServerRODBClass;
import dbgate.ermanagement.*;
import dbgate.ermanagement.exceptions.SequenceGeneratorInitializationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: Oct 2, 2010
 * Time: 10:57:34 PM
 */
public class DBClassAttributeExtractionUtils
{
    public static String getTableName(ServerRODBClass serverRODBClass, Class type)
    {
        String tableName = null;
        if (serverRODBClass instanceof IManagedDBClass)
        {
            Map<Class,String> tableNames = ((IManagedDBClass)serverRODBClass).getTableNames();
            if (tableNames != null
                    && tableNames.containsKey(type))
            {
                tableName = tableNames.get(type);
            }
        }
        if (tableName == null)
        {
            Annotation[] annotations = type.getAnnotations();
            for (Annotation annotation : annotations)
            {
                if (annotation instanceof DBTableInfo)
                {
                    DBTableInfo tableInfo = (DBTableInfo) annotation;
                    tableName = tableInfo.tableName();
                    break;
                }
            }
        }
        return tableName;
    }

    public static Collection<IField> getAllFields(ServerRODBClass serverRODBClass,Class type) throws SequenceGeneratorInitializationException
    {
        Map<Class,Collection<IField>> fieldInfoMap = null;
        if (serverRODBClass instanceof IManagedRODBClass)
        {
            fieldInfoMap = ((IManagedRODBClass)serverRODBClass).getFieldInfo();
        }
        Collection<IField> fields = new ArrayList<IField>();
        Class[] superTypes = ReflectionUtils.getSuperTypesWithInterfacesImplemented(type,new Class[]{ServerRODBClass.class});

        for (int i = 0; i < superTypes.length; i++)
        {
            Class superType = superTypes[i];
            fields.addAll(getAllFields(fieldInfoMap,superType,i > 0));
        }

        return fields;
    }

    private static Collection<IField> getAllFields(Map<Class,Collection<IField>> fieldInfoMap,Class type,boolean superClass) throws SequenceGeneratorInitializationException
    {
        Collection<IField> fields = new ArrayList<IField>();

        Collection<IField> infoFields = fieldInfoMap != null?fieldInfoMap.get(type):null;
        if (infoFields != null)
        {
            for (IField infoField : infoFields)
            {
                if (superClass
                        && infoField instanceof IDBColumn
                        && ((IDBColumn)infoField).isSubClassCommonColumn())
                {
                    fields.add(infoField);
                }
                else if (!superClass)
                {
                    fields.add(infoField);
                }
            }
        }

        Field[] dbClassFields = type.getDeclaredFields();
        for (Field dbClassField : dbClassFields)
        {
            Annotation[] annotations = dbClassField.getAnnotations();
            for (Annotation annotation : annotations)
            {
                if (annotation instanceof DBColumnInfo)
                {
                    DBColumnInfo dbColumnInfo = (DBColumnInfo) annotation;
                    IDBColumn column = createColumnMapping(dbClassField, dbColumnInfo);
                    if (superClass)
                    {
                        if (column.isSubClassCommonColumn())
                        {
                            fields.add(column);
                        }
                    }
                    else
                    {
                        fields.add(column);
                    }
                }
                else if (annotation instanceof ForeignKeyInfo)
                {
                    if (superClass)
                    {
                        continue;
                    }
                    ForeignKeyInfo foreignKeyInfo = (ForeignKeyInfo) annotation;
                    IDBRelation relation = createForeignKeyMapping(dbClassField, foreignKeyInfo);
                    fields.add(relation);
                }
                else if (annotation instanceof ForeignKeyInfoList)
                {
                    if (superClass)
                    {
                        continue;
                    }
                    ForeignKeyInfoList foreignKeyInfoList = (ForeignKeyInfoList) annotation;
                    for (ForeignKeyInfo foreignKeyInfo : foreignKeyInfoList.infoList())
                    {
                        IDBRelation relation = createForeignKeyMapping(dbClassField, foreignKeyInfo);
                        fields.add(relation);
                    }
                }
            }
        }

        return fields;
    }

    private static IDBColumn createColumnMapping(Field dbClassField, DBColumnInfo dbColumnInfo) throws SequenceGeneratorInitializationException
    {
        IDBColumn column = new DefaultDBColumn(dbClassField.getName(),dbColumnInfo.columnType(),dbColumnInfo.nullable());
        if (dbColumnInfo.columnName() != null
                && dbColumnInfo.columnName().trim().length() > 0)
        {
            column.setColumnName(dbColumnInfo.columnName());
        }
        column.setKey(dbColumnInfo.key());
        column.setSize(dbColumnInfo.size());
        column.setSubClassCommonColumn(dbColumnInfo.subClassCommonColumn());
        column.setReadFromSequence(dbColumnInfo.readFromSequence());
        if (column.isReadFromSequence())
        {
            try
            {
                column.setSequenceGenerator((ISequenceGenerator) Class.forName(dbColumnInfo.sequenceGeneratorClassName()).newInstance());
            }
            catch (Exception e)
            {
                
                throw new SequenceGeneratorInitializationException(String.format("Could not initialize sequence generator %s",dbColumnInfo.sequenceGeneratorClassName()),e);
            }
        }
        return column;
    }

    private static IDBRelation createForeignKeyMapping(Field dbClassField, ForeignKeyInfo foreignKeyInfo)
    {
        DBRelationColumnMapping[] objectMappings = new DBRelationColumnMapping[foreignKeyInfo.columnMappings().length];
        ForeignKeyColumnMapping[] annotationMappings = foreignKeyInfo.columnMappings();
        for (int i = 0, columnMappingsLength = annotationMappings.length; i < columnMappingsLength; i++)
        {
            ForeignKeyColumnMapping mapping = annotationMappings[i];
            objectMappings[i] = new DBRelationColumnMapping(mapping.fromField(),mapping.toField());
        }

        IDBRelation relation = new DefaultDBRelation(dbClassField.getName(),foreignKeyInfo.name()
                ,foreignKeyInfo.relatedObjectType(),objectMappings,foreignKeyInfo.updateRule()
                ,foreignKeyInfo.deleteRule(),foreignKeyInfo.reverseRelation()
                ,foreignKeyInfo.nonIdentifyingRelation());

        return relation;
    }
}
