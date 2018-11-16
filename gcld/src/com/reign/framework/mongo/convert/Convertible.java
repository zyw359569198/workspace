package com.reign.framework.mongo.convert;

public interface Convertible<E>
{
    DBObject toDBObject();
    
    E parse(final DBObject p0);
}
