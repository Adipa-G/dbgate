package dbgate.caches.impl;

import dbgate.IColumn;
import dbgate.IRelation;
import dbgate.RelationColumnMapping;

/**
 * Created by IntelliJ IDEA.
 * User: Adipa
 * Date: 9/2/12
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityRelationColumnInfo
{
    private IColumn column;
    private IRelation relation;
    private RelationColumnMapping mapping;

    public EntityRelationColumnInfo(IColumn column, IRelation relation, RelationColumnMapping mapping)
    {
        this.column = column;
        this.relation = relation;
        this.mapping = mapping;
    }

    public IColumn getColumn()
    {
        return column;
    }

    public IRelation getRelation()
    {
        return relation;
    }

    public RelationColumnMapping getMapping()
    {
        return mapping;
    }
}
