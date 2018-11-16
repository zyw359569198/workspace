package com.reign.framework.mongo;

public class DefaultWriteConcernResolver implements WriteConcernResolver
{
    @Override
    public WriteConcern resolve(final MongoActionOperation action) {
        return WriteConcern.NORMAL;
    }
}
