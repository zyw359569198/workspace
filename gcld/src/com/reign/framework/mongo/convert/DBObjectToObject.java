package com.reign.framework.mongo.convert;

public interface DBObjectToObject
{
     <E> E convert(final DBObject p0, final Class<E> p1);
}
