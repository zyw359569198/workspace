package com.reign.framework.mongo.util;

import org.hibernate.mapping.*;
import java.util.*;

public final class MongoDBUtil
{
    private static final String DEFAULT_MAP_COLLECTION_NAME = "map";
    private static final String DEFAULT_LIST_COLLECTION_NAME = "list";
    private static final String DEFAULT_ARRAY_COLLECTION_NAME_PREFIX = "array";
    
    public static String getCollectionName(final Class<?> entityClass) {
        if (entityClass.isAssignableFrom(Map.class)) {
            return "map";
        }
        if (Collection.class.isAssignableFrom(entityClass)) {
            return "list";
        }
        if (entityClass.isArray()) {
            return "array_" + getCollectionName(entityClass.getComponentType());
        }
        return entityClass.getSimpleName();
    }
}
