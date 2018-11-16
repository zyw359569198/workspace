package com.reign.framework.mongo.convert;

public abstract class AbstractConvert implements ObjectToDBObject, DBObjectToObject
{
    public abstract void register(final Class<?> p0, final Object p1);
}
