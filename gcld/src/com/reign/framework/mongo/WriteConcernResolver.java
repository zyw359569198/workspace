package com.reign.framework.mongo;

public interface WriteConcernResolver
{
    WriteConcern resolve(final MongoActionOperation p0);
}
