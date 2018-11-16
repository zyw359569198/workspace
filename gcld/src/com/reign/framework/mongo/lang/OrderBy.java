package com.reign.framework.mongo.lang;

import java.util.*;

public class OrderBy
{
    public String column;
    public boolean asce;
    protected List<OrderBy> orderByList;
    
    public OrderBy add(final OrderBy orderBy) {
        this.orderByList.add(orderBy);
        return this;
    }
    
    public OrderBy(final String column) {
        this.orderByList = new ArrayList<OrderBy>();
        this.column = column;
        this.asce = true;
        this.add(this);
    }
    
    public OrderBy desc() {
        this.asce = false;
        return this;
    }
    
    public List<OrderBy> getOrderBy() {
        return this.orderByList;
    }
    
    public DBObject toDBObject() {
        final DBObject dbObject = (DBObject)new BasicDBObject();
        for (final OrderBy ob : this.orderByList) {
            dbObject.put(ob.column, (Object)(ob.asce ? 1 : -1));
        }
        return dbObject;
    }
}
